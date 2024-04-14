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

import io.grpc.ManagedChannel
import org.apache.kafka.common.utils.CloseableIterator
import org.apache.kyuubi.engine.spark.connect.client.GrpcRetryHandler
import org.apache.kyuubi.engine.spark.connect.proto.{ExecutePlanRequest, ExecutePlanResponse}

class KyuubiSparkConnectBlockingStub(
    channel: ManagedChannel,
    retryPolicy: GrpcRetryHandler.RetryPolicy) {
  private val stub = proto.SparkConnectServiceGrpc.newBlockingStub(channel)

  private val retryHandler = new GrpcRetryHandler(retryPolicy)

  private val grpcExceptionConverter = new GrpcExceptionConverter(stub)

  def executePlan(request: ExecutePlanRequest): CloseableIterator[ExecutePlanResponse] = {
    grpcExceptionConverter.convert(
      request.getSessionId,
      request.getUserContext,
      request.getClientType) {
      grpcExceptionConverter.convertIterator[ExecutePlanResponse](
        request.getSessionId,
        request.getUserContext,
        request.getClientType,
        retryHandler.RetryIterator[ExecutePlanRequest, ExecutePlanResponse](
          request,
          r => CloseableIterator[stub.ex]
        )
      )
    }
  }
}
