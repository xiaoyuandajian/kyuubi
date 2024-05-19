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
package org.apache.kyuubi.grpc.client

import io.grpc.{Grpc, InsecureChannelCredentials}

import org.apache.kyuubi.KyuubiFunSuite
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.grpc.server.SimpleGrpcServer

class ClientSuite extends KyuubiFunSuite {
  private val DEFAULT_PORT = 1000
  private var server: SimpleGrpcServer = _
  private var client: SimpleRpcClient = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val conf = KyuubiConf().set(KyuubiConf.ENGINE_SPARK_CONNECT_GRPC_BINDING_PORT, DEFAULT_PORT)
    server = new SimpleGrpcServer
    server.initialize(conf)
    server.start()
    val channel = Grpc.newChannelBuilderForAddress(
      "localhost",
      DEFAULT_PORT,
      InsecureChannelCredentials.create()).build()
    client = new SimpleRpcClient(channel)
  }

  test("test open session") {
    val response = client.openSession()
  }

  test("test add") {
    val firstNum = 1
    val secondNum = 2
    val response = client.testAdd(firstNum, secondNum)
    info(response)
    assert((5) == response.getResult)
  }
  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }
}
