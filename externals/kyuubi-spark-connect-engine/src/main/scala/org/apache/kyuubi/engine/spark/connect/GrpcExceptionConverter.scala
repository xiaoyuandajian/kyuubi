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
package org.apache.kyuubi.engine.spark.connect

import org.apache.kyuubi.Logging
import org.apache.kyuubi.engine.spark.connect.proto.FetchErrorDetailsResponse.QueryContext
import org.apache.kyuubi.engine.spark.connect.proto.SparkConnectServiceGrpc.SparkConnectServiceBlockingStub
import org.apache.spark.sql.catalyst.parser.ParseException
import org.apache.spark.sql.catalyst.trees.Origin
import org.apache.spark.sql.streaming.StreamingQueryException

import scala.reflect.ClassTag

class GrpcExceptionConverter(stub: SparkConnectServiceBlockingStub) extends Logging {

}

object GrpcExceptionConverter {
  private case class ErrorParams(
      message: String,
      cause: Option[Throwable],
      errorClass: Option[String],
      messageParameters: Map[String, String],
      queryContext: Array[QueryContext])

  private def errorConstructor[T <: Throwable: ClassTag](
      throwableCtr: ErrorParams => T): (String, ErrorParams => Throwable) = {
    val className = implicitly[reflect.ClassTag[T]].runtimeClass.getName
    (className, throwableCtr)
  }

  private val errorFactor = Map()


}
