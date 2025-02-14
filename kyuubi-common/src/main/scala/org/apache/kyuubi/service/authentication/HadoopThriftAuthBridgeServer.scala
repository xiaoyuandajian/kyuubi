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

import java.io.IOException
import java.net.InetAddress
import java.security.PrivilegedAction
import java.util.Base64
import javax.security.auth.callback._
import javax.security.sasl.{AuthorizeCallback, RealmCallback}

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.security.{SaslRpcServer, UserGroupInformation}
import org.apache.hadoop.security.SaslRpcServer.AuthMethod
import org.apache.hadoop.security.token.SecretManager.InvalidToken

import org.apache.kyuubi.Logging
import org.apache.kyuubi.shaded.thrift.{TException, TProcessor}
import org.apache.kyuubi.shaded.thrift.protocol.TProtocol
import org.apache.kyuubi.shaded.thrift.transport._

class HadoopThriftAuthBridgeServer(secretMgr: KyuubiDelegationTokenManager) {
  import HadoopThriftAuthBridgeServer._

  private val ugi = UserGroupInformation.getCurrentUser

  def createSaslServerTransportFactory(
      saslProps: java.util.Map[String, String]): TSaslServerTransport.Factory = {
    val principal = ugi.getUserName
    val names = SaslRpcServer.splitKerberosName(principal)
    if (names.length != 3) {
      throw new TTransportException(s"Kerberos principal should have 3 parts: $principal")
    }
    val factory = new TSaslServerTransport.Factory
    factory.addServerDefinition(
      AuthMethod.KERBEROS.getMechanismName,
      names(0),
      names(1),
      saslProps,
      new SaslRpcServer.SaslGssCallbackHandler)
    factory.addServerDefinition(
      AuthMethod.TOKEN.getMechanismName,
      null,
      SaslRpcServer.SASL_DEFAULT_REALM,
      saslProps,
      new SaslDigestCallbackHandler(secretMgr))
    factory
  }

  /**
   * Wrap a TTransportFactory in such a way that, before processing any RPC, it
   * assumes the UserGroupInformation of the user authenticated by
   * the SASL transport.
   */
  def wrapTransportFactory(transFactory: TTransportFactory): TTransportFactory = {
    new TUGIAssumingTransportFactory(ugi, transFactory)
  }

  /**
   * Wrap a TProcessor to capture the client information like connecting userid, ip etc
   */
  def wrapNonAssumingProcessor(processor: TProcessor): TProcessor = {
    new TUGIAssumingProcessor(processor, secretMgr)
  }

  def getRemoteAddress: InetAddress = REMOTE_ADDRESS.get

  def getRemoteUser: String = REMOTE_USER.get

  def getUserAuthMechanism: String = USER_AUTH_MECHANISM.get
}

object HadoopThriftAuthBridgeServer {

  final val REMOTE_ADDRESS = new ThreadLocal[InetAddress]() {
    override def initialValue(): InetAddress = null
  }

  final val REMOTE_USER = new ThreadLocal[String]() {
    override protected def initialValue: String = null
  }

  final val USER_AUTH_MECHANISM: ThreadLocal[String] = new ThreadLocal[String]() {
    override protected def initialValue: String = AuthMethod.KERBEROS.getMechanismName
  }

  /**
   * Form Apache Hive
   *
   * A TransportFactory that wraps another one, but assumes a specified UGI
   * before calling through.
   *
   * This is used on the server side to assume the server's Principal when accepting
   * clients.
   */
  class TUGIAssumingTransportFactory(
      ugi: UserGroupInformation,
      wrapped: TTransportFactory) extends TTransportFactory {

    override def getTransport(trans: TTransport): TTransport = {
      ugi.doAs(new PrivilegedAction[TTransport] {
        override def run(): TTransport = wrapped.getTransport(trans)
      })
    }
  }

  /**
   * Form Apache Hive
   *
   * Processor that pulls the SaslServer object out of the transport, and
   * assumes the remote user's UGI before calling through to the original
   * processor.
   *
   * This is used on the server side to set the UGI for each specific call.
   */
  class TUGIAssumingProcessor(
      wrapped: TProcessor,
      secretMgr: KyuubiDelegationTokenManager) extends TProcessor with Logging {
    override def process(in: TProtocol, out: TProtocol): Unit = {
      val transport = in.getTransport
      transport match {
        case saslTrans: TSaslServerTransport =>
          val saslServer = saslTrans.getSaslServer
          val authId = saslServer.getAuthorizationID
          var endUser = authId
          debug(s"AUTH ID ======> $authId")
          val socket = saslTrans.getUnderlyingTransport.asInstanceOf[TSocket].getSocket
          REMOTE_ADDRESS.set(socket.getInetAddress)
          val mechanismName = saslServer.getMechanismName
          USER_AUTH_MECHANISM.set(mechanismName)
          try {
            if (AuthMethod.PLAIN.getMechanismName.equalsIgnoreCase(mechanismName)) {
              REMOTE_USER.set(endUser)
              wrapped.process(in, out)
            } else {
              if (AuthMethod.TOKEN.getMechanismName.equalsIgnoreCase(mechanismName)) {
                try {
                  val identifier = SaslRpcServer.getIdentifier(authId, secretMgr)
                  endUser = identifier.getUser.getUserName
                } catch {
                  case e: InvalidToken => throw new TException(e.getMessage)
                }
              }
              val clientUgi: UserGroupInformation = UserGroupInformation.createRemoteUser(endUser)
              try {
                REMOTE_USER.set(clientUgi.getShortUserName)
                debug(s"SET REMOTE USER: ${REMOTE_USER.get()} from endUser: $clientUgi")
                wrapped.process(in, out)
              } catch {
                case e: RuntimeException => e.getCause match {
                    case t: TException => throw t
                    case _ => throw e
                  }
                case e: InterruptedException => throw new RuntimeException(e)
                case e: IOException => throw new RuntimeException(e)
              } finally {
                try {
                  FileSystem.closeAllForUGI(clientUgi)
                } catch {
                  case e: IOException =>
                    error(s"Could not clean up file-system handles for UGI: $clientUgi", e)
                }
              }
            }
          } finally {
            REMOTE_USER.remove()
            REMOTE_ADDRESS.remove()
            USER_AUTH_MECHANISM.remove()
          }

        case _ => throw new TException(s"Unexpected non-SASL transport ${transport.getClass}")
      }
    }
  }

  /**
   * From Apache Hive
   */
  class SaslDigestCallbackHandler(secretMgr: KyuubiDelegationTokenManager)
    extends CallbackHandler with Logging {

    def getPasswd(identifier: KyuubiDelegationTokenIdentifier): Array[Char] = {
      val passwd = secretMgr.retrievePassword(identifier)
      Base64.getEncoder.encodeToString(passwd).toCharArray
    }

    override def handle(callbacks: Array[Callback]): Unit = {
      var nc: NameCallback = null
      var pc: PasswordCallback = null
      callbacks.foreach {
        case ac: AuthorizeCallback =>
          val authenticationID = ac.getAuthenticationID
          val authorizationID = ac.getAuthorizationID
          ac.setAuthorized(authenticationID == authorizationID)
          if (ac.isAuthorized) {
            debug(s"SASL server DIGEST-MD5 callback: setting canonicalized client ID" +
              SaslRpcServer.getIdentifier(authorizationID, secretMgr).getUser.getUserName)
            ac.setAuthorizedID(authorizationID)
          }
        case c: NameCallback => nc = c
        case p: PasswordCallback => pc = p
        case _: RealmCallback => // do nothing
        case o => throw new UnsupportedCallbackException(o, "Unrecognized SASL DIGEST-MD5 Callback")
      }
      if (pc != null) {
        val tokenIdentifier = SaslRpcServer.getIdentifier(nc.getDefaultName, secretMgr)
        val password: Array[Char] = getPasswd(tokenIdentifier)
        debug(s"SASL server DIGEST-MD5 callback: setting password for client:" +
          s" ${tokenIdentifier.getUser}")
        pc.setPassword(password)
      }
    }
  }
}
