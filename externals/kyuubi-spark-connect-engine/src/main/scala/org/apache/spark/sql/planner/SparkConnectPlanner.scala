/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.planner

import scala.jdk.CollectionConverters.{iterableAsScalaIterableConverter, mapAsScalaMapConverter}
import org.apache.spark.connect.proto
import org.apache.spark.sql.catalyst.plans.logical
import org.apache.spark.sql.catalyst.expressions.{Alias, AttributeReference, CaseWhen, Cast, CreateMap, ExprUtils, Expression, In, Literal, NamedExpression, NthValue, aggregate}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.catalyst.analysis.{MultiAlias, UnresolvedAttribute, UnresolvedFunction, UnresolvedRelation, UnresolvedStar}
import org.apache.spark.sql.catalyst.parser.{CatalystSqlParser, ParseException}
import org.apache.spark.sql.catalyst.plans.logical.{LocalRelation, LogicalPlan}
import org.apache.spark.sql.types.{BinaryType, BooleanType, DataType, DoubleType, IntegerType, Metadata, StringType, StructType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.apache.kyuubi.Logging
import org.apache.kyuubi.engine.spark.connect.session.SparkConnectSessionImpl
import org.apache.kyuubi.grpc.session.GrpcSession
import org.apache.spark.Partition
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.connect.common.DataTypeProtoConverter
import org.apache.spark.sql.execution.datasources.LogicalRelation
import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JDBCPartition, JDBCRelation}

final case class InvalidCommandInput(
    private val message: String = "",
    private val cause: Throwable = null)
  extends Exception(message, cause)

class SparkConnectPlanner(val session: GrpcSession) extends Logging {
  def spark: SparkSession = session.asInstanceOf[SparkConnectSessionImpl].spark

  def userId: String = session.sessionKey.userId

  def sessionId: String = session.sessionKey.sessionId

  private lazy val pythonExec =
    sys.env.getOrElse("PYSPARK_PYTHON", sys.env.getOrElse("PYSPARK_DRIVER_PYTHON", "python3"))

  def transformRelation(rel: proto.Relation): LogicalPlan = {
    val plan = rel.getRelTypeCase match {
      case proto.Relation.RelTypeCase.SHOW_STRING => transformShowString(rel.getShowString)
      case proto.Relation.RelTypeCase.HTML_STRING => transformHtmlString(rel.getHtmlString)
      case proto.Relation.RelTypeCase.READ => transformReadRel(rel.getRead)
      case proto.Relation.RelTypeCase.PROJECT => transformProject(rel.getProject)
    }
  }

  private def transformShowString(rel: proto.ShowString): LogicalPlan = {
    val showString = Dataset
      .ofRows(spark, transformRelation(rel.getInput))
      .showString(rel.getNumRows, rel.getTruncate, rel.getVertical)
    LocalRelation.fromProduct(
      output = AttributeReference("show_string", StringType, false)() :: Nil,
      data = Tuple1.apply(showString) :: Nil)
  }

  private def transformHtmlString(rel: proto.HtmlString): LogicalPlan = {
    val htmlString = Dataset
      .ofRows(spark, transformRelation(rel.getInput))
      .htmlString(rel.getNumRows, rel.getTruncate)
    LocalRelation.fromProduct(
      output = AttributeReference("html_string", StringType, false)() :: Nil,
      data = Tuple1.apply(htmlString) :: Nil)
  }

  private def transformReadRel(rel: proto.Read): LogicalPlan = {
    rel.getReadTypeCase match {
      case proto.Read.ReadTypeCase.NAMED_TABLE =>
        val multipartIdentifier =
          CatalystSqlParser.parseMultipartIdentifier(rel.getNamedTable.getUnparsedIdentifier)
        UnresolvedRelation(
          multipartIdentifier,
          new CaseInsensitiveStringMap(rel.getNamedTable.getOptionsMap),
          isStreaming = rel.getIsStreaming)

      case proto.Read.ReadTypeCase.DATA_SOURCE if !rel.getIsStreaming =>
        val localMap =
          CaseInsensitiveMap[String](rel.getDataSource.getOptionsMap.asScala.toMap)
        val reader = spark.read
        if (rel.getDataSource.hasFormat) {
          reader.format(rel.getDataSource.getFormat)
        }
        localMap.foreach{ case (key, value) => reader.option(key, value) }
        if (rel.getDataSource.getFormat == "jdbc" && rel.getDataSource.getPredicatesCount > 0) {
          if (!localMap.contains(JDBCOptions.JDBC_URL) ||
          !localMap.contains(JDBCOptions.JDBC_TABLE_NAME)) {
            throw InvalidPlanInput(s"Invalid jdbc params, please specify jdbc url and table.")
          }

          val url = rel.getDataSource.getOptionsMap.get(JDBCOptions.JDBC_URL)
          val table = rel.getDataSource.getOptionsMap.get(JDBCOptions.JDBC_TABLE_NAME)
          val options = new JDBCOptions(url, table, localMap)
          val predicates = rel.getDataSource.getPredicatesList.asScala.toArray
          val parts: Array[Partition] = predicates.zipWithIndex.map{ case (part, index) =>
            JDBCPartition(part, index): Partition
          }
          val relation = JDBCRelation(parts, options)(spark)
          LogicalRelation(relation)
        } else if (rel.getDataSource.getPredicatesCount == 0) {
          if (rel.getDataSource.hasSchema && rel.getDataSource.getSchema.nonEmpty) {
            reader.schema(parseSchema(rel.getDataSource.getSchema))
          }
          if (rel.getDataSource.getPathsCount == 0) {
            reader.load().queryExecution.analyzed
          } else if (rel.getDataSource.getPathsCount == 1) {
            reader.load(rel.getDataSource.getPaths(0)).queryExecution.analyzed
          } else {
            reader.load(rel.getDataSource.getPathsList.asScala.toSeq: _*).queryExecution.analyzed
          }
        } else {
          throw InvalidPlanInput(
            s"Predicates are not supported for ${rel.getDataSource.getFormat} data sources."
          )
        }

      case proto.Read.ReadTypeCase.DATA_SOURCE if rel.getIsStreaming =>
        val streamSource = rel.getDataSource
        val reader = spark.readStream
        if (streamSource.hasFormat) {
          reader.format(streamSource.getFormat)
        }
        reader.options(streamSource.getOptionsMap.asScala)
        if (streamSource.getSchema.nonEmpty) {
          reader.schema(parseSchema(streamSource.getSchema))
        }
        val streamDF = streamSource.getOptionsCount match {
          case 0 => reader.load()
          case 1 => reader.load(streamSource.getPaths(0))
          case _ =>
            throw InvalidPlanInput(s"Multiple paths are not supported for streaming source")
        }

        streamDF.queryExecution.analyzed

      case _ => throw InvalidPlanInput(s"Does not support ${rel.getReadTypeCase.name()}")
    }
  }

  private def transformProject(rel: proto.Project): LogicalPlan = {
    val baseRel = if (rel.hasInput) {
      transformRelation(rel.getInput)
    } else {
      logical.OneRowRelation()
    }

    val projection = rel.getExpressionsList.asScala.toSeq
      .map(transformExpression)
      .map(toNamedExpression)

    logical.Project(projectList = projection, child = baseRel)
  }


  private def parseSchema(schema: String): StructType = {
    DataType.parseTypeWithFallback(
      schema,
      StructType.fromDDL,
      fallbackParser = DataType.fromJson) match {
      case s: StructType => s
      case o => throw InvalidPlanInput(s"Invalid schema $o")
    }
  }

  def transformExpression(expr: proto.Expression): Expression = {
    expr.getExprTypeCase match {
      case proto.Expression.ExprTypeCase.LITERAL => transformLiteral(expr.getLiteral)
      case proto.Expression.ExprTypeCase.UNRESOLVED_ATTRIBUTE =>
        transformUnresolvedAttribute(expr.getUnresolvedAttribute)
      case proto.Expression.ExprTypeCase.UNRESOLVED_FUNCTION =>
        transformUnresolvedFunction(expr.getUnresolvedFunction)
        .getOrElse(transformUnresolvedFunction(expr.getUnresolvedFunction))
      case proto.Expression.ExprTypeCase.ALIAS => transformAlias(expr.getAlias)
    }
  }

  private def transformLiteral(lit: proto.Expression.Literal): Literal = {
    LiteralExpressionProtoConverter.toCatalystExpression(lit)
  }

  private def transformUnresolvedAttribute(
      attr: proto.Expression.UnresolvedAttribute): UnresolvedAttribute = {
    val expr = UnresolvedAttribute.quotedString(attr.getUnparsedIdentifier)
    if (attr.hasPlanId) {
      expr.setTagValue(LogicalPlan.PLAN_ID_TAG, attr.getPlanId)
    }
    expr
  }

  private def transformUnresolvedFunction(fun: proto.Expression.UnresolvedFunction)
    : Option[Expression] = {
    def extractArgsOfProtobufFunction(
        functionName: String,
        argumentsCount: Int,
        children: collection.Seq[Expression])
    : (String, Option[Array[Byte]], Map[String, String]) = {
      val messageClassName = children(1) match {
        case Literal(s, StringType) if s != null => s.toString
        case o =>
          throw InvalidPlanInput(
            s"MessageClassName in $functionName should be a literal string, " +
              s"but got $o"
          )
      }
      val (binaryFileDescSetOpt, options) = if (argumentsCount == 2) {
        (None, Map.empty[String, String])
      } else if (argumentsCount == 3) {
        children(2) match {
          case Literal(b, BinaryType) if b !=null =>
            (Some(b.asInstanceOf[Array[Byte]]), Map.empty[String, String])
          case UnresolvedFunction(Seq("map"), arguments, _, _, _) =>
            (None, ExprUtils.convertToMapData(CreateMap(arguments)))
          case o =>
            throw InvalidPlanInput(
              s"The valid type for the 3rd arg in $functionName " +
                s"is binary or map, but got $o"
            )
         }
      } else if (argumentsCount == 4) {
        val fileDescSetOpt = children(2) match {
          case Literal(b, BinaryType) if b != null =>
            Some(b.asInstanceOf[Array[Byte]])
          case other =>
            throw InvalidPlanInput(
              s"DescFilePath in $functionName should be a literal binary, " +
                s"but got $other"
            )
        }
        val map = children(3) match {
          case UnresolvedFunction(Seq("map"), arguments, _, _, _) =>
            ExprUtils.convertToMapData(CreateMap(arguments))
          case other =>
            throw InvalidPlanInput(
              s"Options in $functionName should be created by map, " +
                s"but got $other")
        }
        (fileDescSetOpt, map)
      } else {
        throw InvalidPlanInput(
          s"$functionName requires 2~4 arguments, " +
            s"but got $argumentsCount ones!")
      }
      (messageClassName, binaryFileDescSetOpt, options)
    }

    fun.getFunctionName match {
      case "product" if fun.getArgumentsCount == 1 =>
        Some(
          aggregate
            .Product(transformExpression(fun.getArgumentsList.asScala.head))
            .toAggregateExpression())

      case "when" if fun.getArgumentsCount >0 =>
        val children = fun.getArgumentsList.asScala.toSeq.map(transformExpression)
        Some(CaseWhen.createFromParser(children))

      case "in" if fun.getArgumentsCount > 0 =>
        val children = fun.getArgumentsList.asScala.toSeq.map(transformExpression)
        Some(In(children.head, children.tail))

      case "nth_value" if fun.getArgumentsCount == 3 =>
        val children = fun.getArgumentsList.asScala.map(transformExpression)
        val ignoreNulls = extractBoolean(children(2), "ignoreNulls")
        Some(NthValue(children(0), children(1), ignoreNulls))
    }
  }


  private def extractBoolean(expr: Expression, field: String): Boolean = expr match {
    case Literal(bool: Boolean, BooleanType) => bool
    case other => throw InvalidPlanInput(s"$field should be a literal boolean, but got $other")
  }

  private def extractDouble(expr: Expression, field: String): Double = expr match {
    case Literal(double: Double, DoubleType) => double
    case other => throw InvalidPlanInput(s"$field should be a literal double, but got $other")
  }

  private def extractInteger(expr: Expression, field: String): Int = expr match {
    case Literal(int: Int, IntegerType) => int
    case other => throw InvalidPlanInput(s"$field should be a literal integer, but got $other")
  }

  private def extractString(expr: Expression, field: String): String = expr match {
    case Literal(s, StringType) if s != null => s.toString
    case other => throw InvalidPlanInput(s"$field should be a literal string, but got $other")
  }

  private def transformAlias(alias: proto.Expression.Alias): NamedExpression = {
    if (alias.getNameCount == 1) {
      val metadata = if (alias.hasMetadata() && alias.getMetadata.nonEmpty) {
        Some(Metadata.fromJson(alias.getMetadata))
      } else {
        None
      }
      Alias(transformExpression(alias.getExpr), alias.getName(0))(explicitMetadata = metadata)
    } else {
      if (alias.hasMetadata) {
        throw InvalidPlanInput(
          "Alias Expressions with more than 1 identifier must not use optional metadata.")
      }
      MultiAlias(transformExpression(alias.getExpr), alias.getNameList.asScala.toSeq)
    }
  }

  private def transformExpressionString(expr: proto.Expression.ExpressionString): Expression = {
    spark.sessionState.sqlParser.parseExpression(expr.getExpression)
  }

  private def transformUnresolvedStar(star: proto.Expression.UnresolvedStar): UnresolvedStar = {
    if (star.hasUnparsedTarget) {
      val target = star.getUnparsedTarget
      if (!target.endsWith(".*")) {
        throw InvalidPlanInput(
          s"UnresolvedStar requires a unparsed target ending with '.*', " +
            s"but got $target.")
      }
      UnresolvedStar(
        Some(UnresolvedAttribute.parseAttributeName(target.substring(0, target.length - 2))))
    } else {
      UnresolvedStar(None)
    }
  }

  private def transformCast(cast: proto.Expression.Cast): Expression = {
    cast.getCastToTypeCase match {
      case proto.Expression.Cast.CastToTypeCase.TYPE =>
        Cast(transformExpression(cast.getExpr), transformDataType(cast.getType))
    }
  }

  private def transformDataType(t: proto.DataType): DataType = {
    t.getKindCase match {
      case proto.DataType.KindCase.UNPARSED =>
        parseDatatypeString(t.getUnparsed.getDataTypeString)
      case _ => DataTypeProtoConverter.toCatalystType(t)
    }
  }

  private def parseDatatypeString(sqlText: String): DataType = {
    val parser = spark.sessionState.sqlParser
    try {
      parser.parseTableSchema(sqlText)
    } catch {
      case e: ParseException =>
        try {
          parser.parseDataType(sqlText)
        } catch {

        }
    }
  }

  @scala.annotation.tailrec
  private def extractMapData(expr: Expression, field: String): Map[String, String] = expr match {
    case map: CreateMap => ExprUtils.convertToMapData(map)
    case UnresolvedFunction(Seq("map"), args, _, _, _) => extractMapData(CreateMap(args), field)
    case other => throw InvalidPlanInput(s"$field should be created by map, but got $other")
  }
}

case class InvalidPlanInput(
    private val message: String = "",
    private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
