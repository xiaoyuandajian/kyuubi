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

import io.grpc.InternalConfigSelector.Result
import io.grpc.Status
import org.apache.hadoop.conf.Configuration
import org.apache.kyuubi.Logging
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.spark.connect.proto.UserContext
import org.apache.kyuubi.service.TFrontendService.FeServiceServerContext
import org.apache.kyuubi.service.{AbstractFrontendService, Serverable}
import org.apache.kyuubi.util.{KyuubiHadoopUtils, NamedThreadFactory}
import org.apache.spark.connect.proto.SparkConnectServiceGrpc.SparkConnectServiceBlockingStub

import java.util.concurrent.atomic.AtomicBoolean

class KyuubiSparkConnectFrontendService(
    override val serverable: Serverable)
    extends AbstractFrontendService("KyuubiSparkConnectFrontendService") with Runnable with Logging{

  private var stub: SparkConnectServiceBlockingStub = _
  private lazy val _hadoopConf: Configuration = KyuubiHadoopUtils.newHadoopConf(conf)
  private lazy val serverThread = new NamedThreadFactory(getName, false).newThread(this)
  private var userContext: UserContext = _

  override def initialize(conf: KyuubiConf): Unit = synchronized {
    this.conf = conf
    try {

    }
  }

}


private[kyuubi] object KyuubiSparkConnectFrontendService {
  final val OK_STATUS = new Result(Status.OK)

}