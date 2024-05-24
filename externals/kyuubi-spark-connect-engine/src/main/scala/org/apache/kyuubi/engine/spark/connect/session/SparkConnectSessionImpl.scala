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

package org.apache.kyuubi.engine.spark.connect.session

import io.grpc.stub.StreamObserver
import org.apache.kyuubi.engine.spark.connect.events.SparkConnectSessionEventsManager
import org.apache.kyuubi.grpc.events.SessionEventsManager
import org.apache.kyuubi.grpc.session.AbstractGrpcSession
import org.apache.spark.connect.proto.{AddArtifactsRequest, AddArtifactsResponse, AnalyzePlanRequest, AnalyzePlanResponse, ArtifactStatusesRequest, ArtifactStatusesResponse, ConfigRequest, ConfigResponse, ExecutePlanRequest, ExecutePlanResponse, InterruptRequest, InterruptResponse, ReattachExecuteRequest, ReleaseExecuteRequest, ReleaseExecuteResponse}
import org.apache.spark.sql.streaming.StreamingQueryListener
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

class SparkConnectSessionImpl(
    val spark: SparkSession,
    userId: String,
    sessionManager: SparkConnectSessionManager)
  extends AbstractGrpcSession(userId) {

  override def sessionEventsManager: SessionEventsManager = SparkConnectSessionEventsManager(this)

  private lazy val dataFrameCache: ConcurrentMap[String, DataFrame] = new ConcurrentHashMap()

  private lazy val listenerCache: ConcurrentMap[String, StreamingQueryListener] =
    new ConcurrentHashMap()


  def executePlan(
      request: ExecutePlanRequest,
      responseObserver: StreamObserver[ExecutePlanResponse]): Unit = {

  }

  def analyzePlan(
      request: AnalyzePlanRequest,
      responseObserver: StreamObserver[AnalyzePlanResponse]): Unit = {

  }

  def config(
      request: ConfigRequest,
      responseObServer: StreamObserver[ConfigResponse]): Unit = {

  }

  def addArtifacts(responseObserver: StreamObserver[AddArtifactsResponse])
    : StreamObserver[AddArtifactsRequest] = {

  }

  def artifactStatus(
      request: ArtifactStatusesRequest,
      responseObserver: StreamObserver[ArtifactStatusesResponse]): Unit = {

  }

  def interrupt(
      request: InterruptRequest,
      responseObserver: StreamObserver[InterruptResponse]): Unit = {

  }

  def reattach(
      request: ReattachExecuteRequest,
      responseObserver: StreamObserver[ExecutePlanResponse]): Unit = {

  }

  def releaseExecute(
      request: ReleaseExecuteRequest,
      responseObserver: StreamObserver[ReleaseExecuteResponse]): Unit = {

  }


}
