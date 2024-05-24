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


package org.apache.kyuubi.engine.spark.connect.events

import org.apache.kyuubi.engine.spark.connect.session.SparkConnectSessionImpl
import org.apache.kyuubi.grpc.events.SessionEventsManager
import org.apache.kyuubi.grpc.session.GrpcSession
import org.apache.kyuubi.grpc.utils.SystemClock
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.events.SessionEventsUtil

case class SparkConnectSessionEventsManager(
    session: GrpcSession) extends SessionEventsManager(session, new SystemClock()) {

  private def spark: SparkSession = session.asInstanceOf[SparkConnectSessionImpl].spark

  override def postStarted(): Unit = {
    super.postStarted()
    SessionEventsUtil(session.sessionKey, spark, clock)
      .handleStarted()
  }

  override def postClosed(): Unit = {
    super.postClosed()
    SessionEventsUtil(session.sessionKey, spark, clock)
      .handleClosed()
  }
}
