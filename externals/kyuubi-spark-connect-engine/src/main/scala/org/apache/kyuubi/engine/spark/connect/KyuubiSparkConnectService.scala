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

import io.grpc.BindableService
import org.apache.kyuubi.Logging
import org.apache.kyuubi.engine.spark.connect.proto.SparkConnectServiceGrpc.AsyncService

class KyuubiSparkConnectService extends AsyncService with BindableService with Logging{

}

object KyuubiSparkConnectService extends Logging {
  private val CACHE_SIZE = 100
  private val CACHE_TIMEOUT_SECONDS = 3600
  private type SessionCacheKey = (String, String)

}
