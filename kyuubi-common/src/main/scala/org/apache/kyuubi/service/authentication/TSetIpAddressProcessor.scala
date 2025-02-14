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

package org.apache.kyuubi.service.authentication

import org.apache.kyuubi.Logging
import org.apache.kyuubi.shaded.hive.service.rpc.thrift.TCLIService.{Iface, Processor}
import org.apache.kyuubi.shaded.thrift.TException
import org.apache.kyuubi.shaded.thrift.protocol.TProtocol
import org.apache.kyuubi.shaded.thrift.transport.{TSaslClientTransport, TSaslServerTransport, TSocket, TTransport}

class TSetIpAddressProcessor[I <: Iface](
    iface: Iface) extends Processor[Iface](iface) with Logging {
  import TSetIpAddressProcessor._

  @throws[TException]
  override def process(in: TProtocol, out: TProtocol): Unit = {
    setIpAddress(in)
    setUserName(in)
    try {
      super.process(in, out)
    } finally {
      THREAD_LOCAL_USER_NAME.remove()
      THREAD_LOCAL_IP_ADDRESS.remove()
    }
  }

  private def setUserName(in: TProtocol): Unit = {
    val transport = in.getTransport
    transport match {
      case transport1: TSaslServerTransport =>
        val userName = transport1.getSaslServer.getAuthorizationID
        THREAD_LOCAL_USER_NAME.set(userName)
      case _ =>
    }
  }

  private def setIpAddress(in: TProtocol): Unit = {
    val transport = in.getTransport
    val tSocket = getUnderlyingSocketFromTransport(transport)
    if (tSocket == null) {
      warn("Unknown Transport, cannot determine ipAddress")
    } else {
      THREAD_LOCAL_IP_ADDRESS.set(tSocket.getSocket.getInetAddress.getHostAddress)
    }
  }

  @scala.annotation.tailrec
  private def getUnderlyingSocketFromTransport(transport: TTransport): TSocket = transport match {
    case t: TSaslServerTransport => getUnderlyingSocketFromTransport(t.getUnderlyingTransport)
    case t: TSaslClientTransport => getUnderlyingSocketFromTransport(t.getUnderlyingTransport)
    case t: TSocket => t
    case _ => null
  }

}

object TSetIpAddressProcessor {
  private val THREAD_LOCAL_IP_ADDRESS = new ThreadLocal[String]() {
    override protected def initialValue: String = null
  }
  private val THREAD_LOCAL_USER_NAME = new ThreadLocal[String]() {
    override protected def initialValue: String = null
  }

  def getUserIpAddress: String = THREAD_LOCAL_IP_ADDRESS.get

  def getUserName: String = THREAD_LOCAL_USER_NAME.get
}
