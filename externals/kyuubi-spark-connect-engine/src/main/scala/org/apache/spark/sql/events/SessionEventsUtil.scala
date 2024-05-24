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

package org.apache.spark.sql.events

import org.apache.kyuubi.grpc.session.SessionKey
import org.apache.kyuubi.grpc.utils.Clock
import org.apache.spark.scheduler.SparkListenerEvent
import org.apache.spark.sql.SparkSession

case class SessionEventsUtil (key: SessionKey,
                              spark: SparkSession,
                              clock: Clock) {

  def handleStarted(): Unit = {
    spark.sparkContext.listenerBus
      .post(
        SparkListenerConnectSessionStarted(
          key.sessionId,
          key.userId,
          clock.getTimeMillis()
        )
      )
  }

  def handleClosed(): Unit = {
    spark.sparkContext.listenerBus
      .post(
        SparkListenerConnectSessionClosed(
          key.sessionId,
          key.userId,
          clock.getTimeMillis()
        )
      )
  }
}

case class SparkListenerConnectSessionStarted(
    sessionId: String,
    userId: String,
    eventTime: Long,
    extraTags: Map[String, String] = Map.empty)
    extends SparkListenerEvent

case class SparkListenerConnectSessionClosed(
    sessionId: String,
    userId: String,
    eventTime: Long,
    extraTags: Map[String, String] = Map.empty)
    extends SparkListenerEvent
