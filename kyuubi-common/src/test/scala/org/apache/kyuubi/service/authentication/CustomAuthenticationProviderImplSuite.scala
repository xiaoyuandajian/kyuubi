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

import javax.security.sasl.AuthenticationException

import org.apache.kyuubi.KyuubiFunSuite
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.service.authentication.AuthenticationProviderFactory.{getAuthenticationProvider, getHttpBearerAuthenticationProvider}

class CustomAuthenticationProviderImplSuite extends KyuubiFunSuite {
  test("Test user defined authentication") {
    val conf = KyuubiConf()

    val e1 = intercept[IllegalArgumentException](
      getAuthenticationProvider(AuthMethods.withName("CUSTOM"), conf))
    assert(e1.getMessage.contains(
      "kyuubi.authentication.custom.class must be set when auth method was CUSTOM."))

    conf.set(
      KyuubiConf.AUTHENTICATION_CUSTOM_CLASS,
      classOf[UserDefineAuthenticationProviderImpl].getCanonicalName)
    val p1 = getAuthenticationProvider(AuthMethods.withName("CUSTOM"), conf)
    val e2 = intercept[AuthenticationException](p1.authenticate("test", "test"))
    assert(e2.getMessage.contains("Username or password is not valid!"))

    p1.authenticate("user", "password")
  }

  test("Test user defined http bearer authentication") {
    val conf = KyuubiConf()

    val e1 = intercept[IllegalArgumentException](
      getHttpBearerAuthenticationProvider("", conf))
    assert(e1.getMessage.contains(
      "kyuubi.authentication.custom.bearer.class must be set for http bearer authentication."))

    conf.set(
      KyuubiConf.AUTHENTICATION_CUSTOM_BEARER_CLASS,
      classOf[UserDefineAuthenticationProviderImpl].getCanonicalName)
    val p1 = getHttpBearerAuthenticationProvider(
      classOf[UserDefineAuthenticationProviderImpl].getCanonicalName,
      conf)
    val credential = DefaultTokenCredential("test", Map.empty)
    val e2 = intercept[AuthenticationException](p1.authenticate(credential))
    assert(e2.getMessage.contains("Token is not valid!"))

    val credential2 = DefaultTokenCredential("token", Map.empty)
    p1.authenticate(credential2)
  }
}
