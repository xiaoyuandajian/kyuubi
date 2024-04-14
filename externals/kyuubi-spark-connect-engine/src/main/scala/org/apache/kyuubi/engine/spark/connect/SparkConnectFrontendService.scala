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

import io.grpc.{ChannelCredentials, ClientInterceptor, Grpc, InsecureChannelCredentials, ManagedChannel}
import org.apache.kafka.common.utils.CloseableIterator
import org.apache.kyuubi.Logging
import org.apache.kyuubi.service.{AbstractFrontendService, Serverable, Service}
import org.apache.kyuubi.session.SessionHandle
import org.apache.kyuubi.shaded.thrift.server.ServerContext
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.spark.connect.common.config.ConnectCommon

import java.net.URI
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Properties

class SparkConnectFrontendService(name: String)
  extends AbstractFrontendService(name) with Runnable with Logging{
  import proto._

  private val userContext: UserContext = conf.

  def analyze(request: AnalyzePlanRequest): AnalyzePlanResponse

  def execute(plan: Plan): CloseableIterator[ExecutePlanResponse]

  def config(operation: ConfigRequest.Operation): ConfigResponse

  def analyze(method: AnalyzePlanRequest.AnalyzeCase,
              plan: Option[Plan] = None,
              explainMode: Option[AnalyzePlanRequest.Explain.ExplainMode] = None)
              : AnalyzePlanResponse

  def interruptAll(): InterruptResponse

  def interruptTag(tag: String): InterruptResponse

  def interruptOperation(id: String): InterruptResponse

  def addArtifact(path: String): Unit

  def addArtifact(uri: URI): Unit

  def addArtifacts(uri: Seq[URI]): Unit
}

object SparkConnectFrontendService {

  private val SPARK_CONNECT_REMOTE: String = "SPARK_CONNECT_REMOTE"

  private val DEFAULT_USER_AGENT: String = "_SPARK_CONNECT_SCALA"

  private def getUserAgent(value: String): String = {
    val scalaVersion = Properties.versionNumberString
    val jvmVersion = System.getProperty("java.version").split("-")(0)
    val osName = {
      val os = System.getProperty("os.name").toLowerCase(Locale.ROOT)
      if (os.contains("mac")) "darwin"
      else if (os.contains("linux")) "linux"
      else if (os.contains("win")) "windows"
      else "unknown"
    }
    List(
      value,
      s"scala/$scalaVersion",
      s"jvm/$jvmVersion",
      s"os/$osName").mkString(" ")
  }
  case class Configuration(
      userId: String = null,
      userName: String = null,
      host: String = "localhost",
      port: Int = ConnectCommon.CONNECT_GRPC_BINDING_PORT,
      token: Option[String] = None,
      isSslEnabled: Option[String] = None,
      metadata: Map[String, String] = Map.empty, // Currently useless
      userAgent: String = getUserAgent(
        sys.env.getOrElse("SPARK_CONNECT_USER_AGENT", DEFAULT_USER_AGENT)),
      useReattachableExecute: Boolean = true,
      interceptors: List[ClientInterceptor] = List.empty, // Currently useless
      sessionId: Option[String] = None) {

    def userContext: proto.UserContext = {
      val builder = proto.UserContext.newBuilder()
      if (userId != null) {
        builder.setUserId(userId)
      }
      if (userName != null) {
        builder.setUserName(userName)
      }
      builder.build()
    }

    def credentials: ChannelCredentials = {
      // default return first
      InsecureChannelCredentials.create()
    }

    def createChannel(): ManagedChannel = {
      val channelBuilder = Grpc.newChannelBuilderForAddress(host, port, credentials)
      channelBuilder.maxInboundMessageSize(ConnectCommon.CONNECT_GRPC_MAX_MESSAGE_SIZE)
      channelBuilder.build()
    }

  }
}