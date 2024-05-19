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
package org.apache.kyuubi.grpc.server

import org.apache.kyuubi.KyuubiFunSuite
import org.apache.kyuubi.config.KyuubiConf

class GrpcSeverableSuite extends KyuubiFunSuite {
  ignore("GrpcSeverable") {
    val severable1 = new SimpleGrpcServer()
    val conf = KyuubiConf().set(KyuubiConf.ENGINE_SPARK_CONNECT_GRPC_BINDING_PORT, 0)
    severable1.initialize(conf)
    assert(severable1.getStartTime === 0)
    assert(severable1.getConf === conf)
    assert(severable1.frontendServices.head.connectionUrl.nonEmpty)
  }

  test("invalid port") {
    val conf = KyuubiConf().set(KyuubiConf.ENGINE_SPARK_CONNECT_GRPC_BINDING_PORT, 10000000)
    val server = new SimpleGrpcServer
    server.initialize(conf)
    server.start()
  }
}
