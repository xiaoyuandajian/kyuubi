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

package org.apache.kyuubi.engine.spark.connect.operation

import org.apache.kyuubi.engine.spark.connect.session.SparkConnectSessionImpl
import org.apache.kyuubi.grpc.events.OperationEventsManager
import org.apache.kyuubi.grpc.operation.{AbstractGrpcOperation, OperationKey}
import org.apache.kyuubi.operation.log.OperationLog

class SparkConnectOperationImpl(session: SparkConnectSessionImpl)
  extends AbstractGrpcOperation[SparkConnectSessionImpl](session) {

  override protected def key: OperationKey = OperationKey(session.sessionKey)

  override protected def runInternal(): Unit = {}

  override protected def beforeRun(): Unit = {

  }

  override protected def afterRun(): Unit = {

  }

  override def close(): Unit = {

  }

  override def interrupt(): Unit = {

  }

  override def getOperationLog: Option[OperationLog] = None

  override def isTimedOut: Boolean = {

  }

  override def operationEventsManager: OperationEventsManager = {

  }
}
