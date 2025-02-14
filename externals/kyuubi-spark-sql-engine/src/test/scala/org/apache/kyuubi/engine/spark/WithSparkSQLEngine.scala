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

package org.apache.kyuubi.engine.spark

import org.apache.spark.sql.SparkSession

import org.apache.kyuubi.{KyuubiFunSuite, Utils}
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.spark.KyuubiSparkUtil.SPARK_ENGINE_RUNTIME_VERSION

trait WithSparkSQLEngine extends KyuubiFunSuite {
  protected var spark: SparkSession = _
  protected var engine: SparkSQLEngine = _
  // conf will be loaded until start spark engine
  def withKyuubiConf: Map[String, String]
  def kyuubiConf: KyuubiConf = SparkSQLEngine.kyuubiConf

  protected var connectionUrl: String = _

  // Behavior is affected by the initialization SQL: 'SHOW DATABASES'
  // SPARK-35378 (3.2.0) makes it triggers job
  // SPARK-43124 (4.0.0) makes it avoid triggering job
  protected val initJobId: Int = if (SPARK_ENGINE_RUNTIME_VERSION >= "4.0") 0 else 1

  override def beforeAll(): Unit = {
    startSparkEngine()
    super.beforeAll()
  }

  def startSparkEngine(): Unit = {
    val warehousePath = Utils.createTempDir()
    val metastorePath = Utils.createTempDir()
    warehousePath.toFile.delete()
    metastorePath.toFile.delete()
    System.setProperty(
      "javax.jdo.option.ConnectionURL",
      s"jdbc:derby:;databaseName=$metastorePath;create=true")
    System.setProperty("spark.sql.warehouse.dir", warehousePath.toString)
    System.setProperty("spark.sql.hive.metastore.sharedPrefixes", "org.apache.hive.jdbc")
    System.setProperty("spark.ui.enabled", "false")
    withKyuubiConf.foreach { case (k, v) =>
      System.setProperty(k, v)
    }

    SparkSession.getActiveSession.foreach(_.close())
    SparkSession.getDefaultSession.foreach(_.close())
    SparkSession.clearActiveSession()
    SparkSession.clearDefaultSession()
    SparkSQLEngine.setupConf()
    spark = SparkSQLEngine.createSpark()
    SparkSQLEngine.startEngine(spark)
    engine = SparkSQLEngine.currentEngine.get
    connectionUrl = engine.frontendServices.head.connectionUrl
  }

  override def afterAll(): Unit = {
    super.afterAll()
    stopSparkEngine()
  }

  def stopSparkEngine(): Unit = {
    // we need to clean up conf since it's the global config in same jvm.
    withKyuubiConf.foreach { case (k, _) =>
      System.clearProperty(k)
    }

    if (engine != null) {
      engine.stop()
      engine = null
    }
    if (spark != null) {
      spark.stop()
      spark = null
    }
    SparkSession.clearActiveSession()
    SparkSession.clearDefaultSession()
  }

  protected def getJdbcUrl: String = s"jdbc:hive2://$connectionUrl/;"
  def getSpark: SparkSession = spark
}
