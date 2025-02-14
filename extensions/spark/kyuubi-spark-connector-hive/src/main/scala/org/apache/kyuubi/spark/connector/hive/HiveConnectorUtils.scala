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

package org.apache.kyuubi.spark.connector.hive

import java.lang.{Boolean => JBoolean, Long => JLong}

import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.hadoop.hive.ql.plan.{FileSinkDesc, TableDesc}
import org.apache.spark.SPARK_VERSION
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.catalog.{CatalogTable, CatalogTablePartition}
import org.apache.spark.sql.connector.catalog.TableChange
import org.apache.spark.sql.connector.catalog.TableChange.{AddColumn, After, ColumnPosition, DeleteColumn, First, RenameColumn, UpdateColumnComment, UpdateColumnNullability, UpdateColumnPosition, UpdateColumnType}
import org.apache.spark.sql.execution.command.CommandUtils
import org.apache.spark.sql.execution.command.CommandUtils.{calculateMultipleLocationSizes, calculateSingleLocationSize}
import org.apache.spark.sql.execution.datasources.{PartitionDirectory, PartitionedFile}
import org.apache.spark.sql.hive.execution.HiveFileFormat
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.types.{ArrayType, MapType, StructField, StructType}

import org.apache.kyuubi.spark.connector.common.SparkUtils.SPARK_RUNTIME_VERSION
import org.apache.kyuubi.util.reflect.{DynClasses, DynConstructors, DynMethods}
import org.apache.kyuubi.util.reflect.ReflectUtils.invokeAs

object HiveConnectorUtils extends Logging {

  // SPARK-43186
  def getHiveFileFormat(fileSinkConf: FileSinkDesc): HiveFileFormat = {
    if (SPARK_RUNTIME_VERSION >= "3.5") {
      DynConstructors.builder()
        .impl(classOf[HiveFileFormat], classOf[FileSinkDesc])
        .build[HiveFileFormat]()
        .newInstance(fileSinkConf)
    } else if (SPARK_RUNTIME_VERSION >= "3.3") {
      val shimFileSinkDescClz = DynClasses.builder()
        .impl("org.apache.spark.sql.hive.HiveShim$ShimFileSinkDesc")
        .build()
      val shimFileSinkDesc = DynConstructors.builder()
        .impl(
          "org.apache.spark.sql.hive.HiveShim$ShimFileSinkDesc",
          classOf[String],
          classOf[TableDesc],
          classOf[Boolean])
        .build[AnyRef]()
        .newInstance(
          fileSinkConf.getDirName.toString,
          fileSinkConf.getTableInfo,
          fileSinkConf.getCompressed.asInstanceOf[JBoolean])
      DynConstructors.builder()
        .impl(classOf[HiveFileFormat], shimFileSinkDescClz)
        .build[HiveFileFormat]()
        .newInstance(shimFileSinkDesc)
    } else {
      throw unsupportedSparkVersion()
    }
  }

  // SPARK-41970
  def partitionedFilePath(file: PartitionedFile): String = {
    if (SPARK_RUNTIME_VERSION >= "3.4") {
      invokeAs[String](file, "urlEncodedPath")
    } else if (SPARK_RUNTIME_VERSION >= "3.3") {
      invokeAs[String](file, "filePath")
    } else {
      throw unsupportedSparkVersion()
    }
  }

  def splitFiles(
      sparkSession: SparkSession,
      file: AnyRef,
      filePath: Path,
      isSplitable: Boolean,
      maxSplitBytes: Long,
      partitionValues: InternalRow): Seq[PartitionedFile] = {

    if (SPARK_RUNTIME_VERSION >= "4.0") { // SPARK-42821
      val fileStatusWithMetadataClz = DynClasses.builder()
        .impl("org.apache.spark.sql.execution.datasources.FileStatusWithMetadata")
        .build()
      DynMethods
        .builder("splitFiles")
        .impl(
          "org.apache.spark.sql.execution.PartitionedFileUtil",
          fileStatusWithMetadataClz,
          classOf[Boolean],
          classOf[Long],
          classOf[InternalRow])
        .build()
        .invoke[Seq[PartitionedFile]](
          null,
          file,
          isSplitable.asInstanceOf[JBoolean],
          maxSplitBytes.asInstanceOf[JLong],
          partitionValues)
    } else if (SPARK_RUNTIME_VERSION >= "3.5") { // SPARK-43039
      val fileStatusWithMetadataClz = DynClasses.builder()
        .impl("org.apache.spark.sql.execution.datasources.FileStatusWithMetadata")
        .build()
      DynMethods
        .builder("splitFiles")
        .impl(
          "org.apache.spark.sql.execution.PartitionedFileUtil",
          classOf[SparkSession],
          fileStatusWithMetadataClz,
          classOf[Boolean],
          classOf[Long],
          classOf[InternalRow])
        .build()
        .invoke[Seq[PartitionedFile]](
          null,
          sparkSession,
          file,
          isSplitable.asInstanceOf[JBoolean],
          maxSplitBytes.asInstanceOf[JLong],
          partitionValues)
    } else if (SPARK_RUNTIME_VERSION >= "3.3") {
      DynMethods
        .builder("splitFiles")
        .impl(
          "org.apache.spark.sql.execution.PartitionedFileUtil",
          classOf[SparkSession],
          classOf[FileStatus],
          classOf[Path],
          classOf[Boolean],
          classOf[Long],
          classOf[InternalRow])
        .build()
        .invoke[Seq[PartitionedFile]](
          null,
          sparkSession,
          file,
          filePath,
          isSplitable.asInstanceOf[JBoolean],
          maxSplitBytes.asInstanceOf[JLong],
          partitionValues)
    } else {
      throw unsupportedSparkVersion()
    }
  }

  def createPartitionDirectory(values: InternalRow, files: Seq[FileStatus]): PartitionDirectory = {
    if (SPARK_RUNTIME_VERSION >= "3.5") {
      new DynMethods.Builder("apply")
        .impl(classOf[PartitionDirectory], classOf[InternalRow], classOf[Array[FileStatus]])
        .buildChecked()
        .asStatic()
        .invoke[PartitionDirectory](values, files.toArray)
    } else if (SPARK_RUNTIME_VERSION >= "3.3") {
      new DynMethods.Builder("apply")
        .impl(classOf[PartitionDirectory], classOf[InternalRow], classOf[Seq[FileStatus]])
        .buildChecked()
        .asStatic()
        .invoke[PartitionDirectory](values, files)
    } else {
      throw unsupportedSparkVersion()
    }
  }

  def getPartitionFilePath(file: AnyRef): Path = {
    if (SPARK_RUNTIME_VERSION >= "3.5") {
      new DynMethods.Builder("getPath")
        .impl("org.apache.spark.sql.execution.datasources.FileStatusWithMetadata")
        .build()
        .invoke[Path](file)
    } else if (SPARK_RUNTIME_VERSION >= "3.3") {
      file.asInstanceOf[FileStatus].getPath
    } else {
      throw unsupportedSparkVersion()
    }
  }

  private def unsupportedSparkVersion(): KyuubiHiveConnectorException = {
    KyuubiHiveConnectorException(s"Spark version $SPARK_VERSION " +
      "is not supported by Kyuubi spark hive connector.")
  }

  def calculateTotalSize(
      spark: SparkSession,
      catalogTable: CatalogTable,
      hiveTableCatalog: HiveTableCatalog): (BigInt, Seq[CatalogTablePartition]) = {
    val sessionState = spark.sessionState
    val startTime = System.nanoTime()
    val (totalSize, newPartitions) = if (catalogTable.partitionColumnNames.isEmpty) {
      (
        calculateSingleLocationSize(
          sessionState,
          catalogTable.identifier,
          catalogTable.storage.locationUri),
        Seq())
    } else {
      // Calculate table size as a sum of the visible partitions. See SPARK-21079
      val partitions = hiveTableCatalog.listPartitions(catalogTable.identifier)
      logInfo(s"Starting to calculate sizes for ${partitions.length} partitions.")
      val paths = partitions.map(_.storage.locationUri)
      val sizes = calculateMultipleLocationSizes(spark, catalogTable.identifier, paths)
      val newPartitions = partitions.zipWithIndex.flatMap { case (p, idx) =>
        val newStats = CommandUtils.compareAndGetNewStats(p.stats, sizes(idx), None)
        newStats.map(_ => p.copy(stats = newStats))
      }
      (sizes.sum, newPartitions)
    }
    logInfo(s"It took ${(System.nanoTime() - startTime) / (1000 * 1000)} ms to calculate" +
      s" the total size for table ${catalogTable.identifier}.")
    (totalSize, newPartitions)
  }

  def applySchemaChanges(schema: StructType, changes: Seq[TableChange]): StructType = {
    changes.foldLeft(schema) { (schema, change) =>
      change match {
        case add: AddColumn =>
          add.fieldNames match {
            case Array(name) =>
              val field = StructField(name, add.dataType, nullable = add.isNullable)
              val newField = Option(add.comment).map(field.withComment).getOrElse(field)
              addField(schema, newField, add.position())

            case names =>
              replace(
                schema,
                names.init,
                parent =>
                  parent.dataType match {
                    case parentType: StructType =>
                      val field = StructField(names.last, add.dataType, nullable = add.isNullable)
                      val newField = Option(add.comment).map(field.withComment).getOrElse(field)
                      Some(parent.copy(dataType = addField(parentType, newField, add.position())))

                    case _ =>
                      throw new IllegalArgumentException(s"Not a struct: ${names.init.last}")
                  })
          }

        case rename: RenameColumn =>
          replace(
            schema,
            rename.fieldNames,
            field =>
              Some(StructField(rename.newName, field.dataType, field.nullable, field.metadata)))

        case update: UpdateColumnType =>
          replace(
            schema,
            update.fieldNames,
            field => Some(field.copy(dataType = update.newDataType)))

        case update: UpdateColumnNullability =>
          replace(
            schema,
            update.fieldNames,
            field => Some(field.copy(nullable = update.nullable)))

        case update: UpdateColumnComment =>
          replace(
            schema,
            update.fieldNames,
            field => Some(field.withComment(update.newComment)))

        case update: UpdateColumnPosition =>
          def updateFieldPos(struct: StructType, name: String): StructType = {
            val oldField = struct.fields.find(_.name == name).getOrElse {
              throw new IllegalArgumentException("Field not found: " + name)
            }
            val withFieldRemoved = StructType(struct.fields.filter(_ != oldField))
            addField(withFieldRemoved, oldField, update.position())
          }

          update.fieldNames() match {
            case Array(name) =>
              updateFieldPos(schema, name)
            case names =>
              replace(
                schema,
                names.init,
                parent =>
                  parent.dataType match {
                    case parentType: StructType =>
                      Some(parent.copy(dataType = updateFieldPos(parentType, names.last)))
                    case _ =>
                      throw new IllegalArgumentException(s"Not a struct: ${names.init.last}")
                  })
          }

        case delete: DeleteColumn =>
          replace(schema, delete.fieldNames, _ => None, delete.ifExists)

        case _ =>
          // ignore non-schema changes
          schema
      }
    }
  }

  private def addField(
      schema: StructType,
      field: StructField,
      position: ColumnPosition): StructType = {
    if (position == null) {
      schema.add(field)
    } else if (position.isInstanceOf[First]) {
      StructType(field +: schema.fields)
    } else {
      val afterCol = position.asInstanceOf[After].column()
      val fieldIndex = schema.fields.indexWhere(_.name == afterCol)
      if (fieldIndex == -1) {
        throw new IllegalArgumentException("AFTER column not found: " + afterCol)
      }
      val (before, after) = schema.fields.splitAt(fieldIndex + 1)
      StructType(before ++ (field +: after))
    }
  }

  private def replace(
      struct: StructType,
      fieldNames: Seq[String],
      update: StructField => Option[StructField],
      ifExists: Boolean = false): StructType = {

    val posOpt = fieldNames.zipWithIndex.toMap.get(fieldNames.head)
    if (posOpt.isEmpty) {
      if (ifExists) {
        // We couldn't find the column to replace, but with IF EXISTS, we will silence the error
        // Currently only DROP COLUMN may pass down the IF EXISTS parameter
        return struct
      } else {
        throw new IllegalArgumentException(s"Cannot find field: ${fieldNames.head}")
      }
    }

    val pos = posOpt.get
    val field = struct.fields(pos)
    val replacement: Option[StructField] = (fieldNames.tail, field.dataType) match {
      case (Seq(), _) =>
        update(field)

      case (names, struct: StructType) =>
        val updatedType: StructType = replace(struct, names, update, ifExists)
        Some(StructField(field.name, updatedType, field.nullable, field.metadata))

      case (Seq("key"), map @ MapType(keyType, _, _)) =>
        val updated = update(StructField("key", keyType, nullable = false))
          .getOrElse(throw new IllegalArgumentException(s"Cannot delete map key"))
        Some(field.copy(dataType = map.copy(keyType = updated.dataType)))

      case (Seq("key", names @ _*), map @ MapType(keyStruct: StructType, _, _)) =>
        Some(field.copy(dataType = map.copy(keyType = replace(keyStruct, names, update, ifExists))))

      case (Seq("value"), map @ MapType(_, mapValueType, isNullable)) =>
        val updated = update(StructField("value", mapValueType, nullable = isNullable))
          .getOrElse(throw new IllegalArgumentException(s"Cannot delete map value"))
        Some(field.copy(dataType = map.copy(
          valueType = updated.dataType,
          valueContainsNull = updated.nullable)))

      case (Seq("value", names @ _*), map @ MapType(_, valueStruct: StructType, _)) =>
        Some(field.copy(dataType = map.copy(valueType =
          replace(valueStruct, names, update, ifExists))))

      case (Seq("element"), array @ ArrayType(elementType, isNullable)) =>
        val updated = update(StructField("element", elementType, nullable = isNullable))
          .getOrElse(throw new IllegalArgumentException(s"Cannot delete array element"))
        Some(field.copy(dataType = array.copy(
          elementType = updated.dataType,
          containsNull = updated.nullable)))

      case (Seq("element", names @ _*), array @ ArrayType(elementStruct: StructType, _)) =>
        Some(field.copy(dataType = array.copy(elementType =
          replace(elementStruct, names, update, ifExists))))

      case (names, dataType) =>
        if (!ifExists) {
          throw new IllegalArgumentException(
            s"Cannot find field: ${names.head} in ${dataType.simpleString}")
        }
        None
    }

    val newFields = struct.fields.zipWithIndex.flatMap {
      case (_, index) if pos == index =>
        replacement
      case (other, _) =>
        Some(other)
    }

    new StructType(newFields)
  }

  // This is a fork of Spark's withSQLConf, and we use a different name to avoid linkage
  // issue on cross-version cases.
  // For example, SPARK-46227(4.0.0) moves `withSQLConf` from SQLHelper to SQLConfHelper,
  // classes that extend SQLConfHelper will prefer to linkage super class's method when
  // compiling with Spark 4.0, then linkage error will happen when run the jar with lower
  // Spark versions.
  def withSparkSQLConf[T](pairs: (String, String)*)(f: => T): T = {
    val conf = SQLConf.get
    val (keys, values) = pairs.unzip
    val currentValues = keys.map { key =>
      if (conf.contains(key)) {
        Some(conf.getConfString(key))
      } else {
        None
      }
    }
    (keys, values).zipped.foreach { (k, v) =>
      if (SQLConf.isStaticConfigKey(k)) {
        throw KyuubiHiveConnectorException(s"Cannot modify the value of a static config: $k")
      }
      conf.setConfString(k, v)
    }
    try f
    finally {
      keys.zip(currentValues).foreach {
        case (key, Some(value)) => conf.setConfString(key, value)
        case (key, None) => conf.unsetConf(key)
      }
    }
  }
}
