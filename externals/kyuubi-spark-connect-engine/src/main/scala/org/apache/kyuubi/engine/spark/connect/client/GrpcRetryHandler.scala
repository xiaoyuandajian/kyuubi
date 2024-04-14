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
package org.apache.kyuubi.engine.spark.connect.client

import io.grpc.{Status, StatusRuntimeException}
import org.apache.kyuubi.Logging

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Random
import scala.util.control.NonFatal

class GrpcRetryHandler(private val retryPolicy: GrpcRetryHandler.RetryPolicy,
                       private val sleep: Long => Unit = Thread.sleep) {

}

object GrpcRetryHandler extends Logging{

  final def retry[T](retryPolicy: RetryPolicy, sleep: Long => Unit = Thread.sleep)(
      fn: => T): T = {
    var currentRetryNum = 0
    var exceptionList: Seq[Throwable] = Seq.empty
    var nextBackoff: Duration = retryPolicy.initialBackoff

    if (retryPolicy.maxRetries < 0) {
      throw new IllegalArgumentException("Can't have negative number of retries")
    }

    while (currentRetryNum <= retryPolicy.maxRetries) {
      if (currentRetryNum != 0) {
        var currentBackoff = nextBackoff
        nextBackoff = nextBackoff * retryPolicy.backoffMultiplier min retryPolicy.maxBackoff

        if (currentBackoff >= retryPolicy.minJitterThreshold) {
          currentBackoff += Random.nextDouble() * retryPolicy.jitter
        }

        sleep(currentBackoff.toMillis)
      }

      try {
        return fn
      } catch {
        case NonFatal(e) if retryPolicy.canRetry(e) && currentRetryNum < retryPolicy.maxBackoff =>
          currentRetryNum += 1
          exceptionList = e +: exceptionList

          if (currentRetryNum <= retryPolicy.maxRetries) {
            logWarning(
              s"Non-Fatal error during RPC execution: $e, " +
                s"retrying (currentRetryNum=$currentRetryNum)")
          } else {
            logWarning(
              s"Non-Fatal error during RPC execution: $e, " +
                s"exceeded retries (currentRetryNum=$currentRetryNum)")
          }
      }
    }
  }
  private def retryException(e: Throwable): Boolean = {
    e match {
      case e: StatusRuntimeException =>
        val statusCode: Status.Code = e.getStatus.getCode

        if (statusCode == Status.Code.INTERNAL) {
          val msg: String = e.toString
          if (msg.contains("INVALID_CURSOR.DISCONNECTED")) {
            return true
          }
        }

        if (statusCode == Status.Code.UNAVAILABLE) {
          return true
        }
        false
      case _ => false
    }
  }
  case class RetryPolicy(
      maxRetries: Int = 15,
      initialBackoff: FiniteDuration = FiniteDuration(50, "ms"),
      maxBackoff: FiniteDuration = FiniteDuration(1, "min"),
      backoffMultiplier: Double = 4.0,
      jitter: FiniteDuration = FiniteDuration(500, "ms"),
      minJitterThreshold: FiniteDuration = FiniteDuration(2, "s"),
      canRetry: Throwable => Boolean = retryException) {}
  class RetryException extends Throwable
}