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

package org.apache.kyuubi

import java.lang.reflect.{InvocationTargetException, UndeclaredThrowableException}
import java.sql.SQLException

import scala.annotation.tailrec
import scala.collection.JavaConverters._

import org.apache.kyuubi.Utils.stringifyException
import org.apache.kyuubi.shaded.hive.service.rpc.thrift.{TStatus, TStatusCode}
import org.apache.kyuubi.util.reflect.DynConstructors

/**
 * @param reason     a description of the exception
 * @param sqlState   an XOPEN or SQL:2003 code identifying the exception
 * @param vendorCode a database vendor-specific exception code
 * @param cause      the underlying reason for this [[SQLException]]
 *                   (which is saved for later retrieval by the `getCause()` method);
 *                   may be null indicating the cause is non-existent or unknown.
 */
class KyuubiSQLException(reason: String, sqlState: String, vendorCode: Int, cause: Throwable)
  extends SQLException(reason, sqlState, vendorCode, cause) {

  // for reflection
  def this(msg: String, cause: Throwable) = this(msg, null, 0, cause)

  /**
   * Converts current object to a [[TStatus]] object
   *
   * @return a { @link TStatus} object
   */
  def toTStatus: TStatus = {
    val tStatus = new TStatus(TStatusCode.ERROR_STATUS)
    tStatus.setSqlState(getSQLState)
    tStatus.setErrorCode(getErrorCode)
    tStatus.setErrorMessage(getMessage)
    tStatus.setInfoMessages(KyuubiSQLException.toString(this).asJava)
    tStatus
  }
}

object KyuubiSQLException {

  final private val HEAD_MARK: String = "*"
  final private val SEPARATOR: Char = ':'

  def apply(
      msg: String,
      cause: Throwable = null,
      sqlState: String = null,
      vendorCode: Int = 0): KyuubiSQLException = {
    new KyuubiSQLException(msg, sqlState, vendorCode, findCause(cause))
  }
  def apply(cause: Throwable): KyuubiSQLException = {
    val theCause = findCause(cause)
    apply(theCause.getMessage, theCause)
  }

  def apply(tStatus: TStatus): KyuubiSQLException = {
    val msg = tStatus.getErrorMessage
    val cause = toCause(tStatus.getInfoMessages.asScala)
    cause match {
      case k: KyuubiSQLException if k.getMessage == msg => k
      case _ => apply(msg, cause, tStatus.getSqlState, tStatus.getErrorCode)
    }
  }

  def featureNotSupported(message: String = "feature not supported"): KyuubiSQLException = {
    KyuubiSQLException(message, sqlState = "0A000")
  }

  def connectionDoesNotExist(): KyuubiSQLException = {
    new KyuubiSQLException("connection does not exist", "08003", 91001, null)
  }

  def toTStatus(e: Exception, verbose: Boolean = false): TStatus = e match {
    case k: KyuubiSQLException => k.toTStatus
    case _ =>
      val tStatus = new TStatus(TStatusCode.ERROR_STATUS)
      val errMsg = if (verbose) stringifyException(e) else e.getMessage
      tStatus.setErrorMessage(errMsg)
      tStatus.setInfoMessages(toString(e).asJava)
      tStatus
  }

  def toString(cause: Throwable): List[String] = {
    toString(cause, null)
  }

  def toString(cause: Throwable, parent: Array[StackTraceElement]): List[String] = {
    val trace = cause.getStackTrace
    var m = trace.length - 1

    if (parent != null) {
      var n = parent.length - 1
      while (m >= 0 && n >= 0 && trace(m).equals(parent(n))) {
        m = m - 1
        n = n - 1
      }
    }

    enroll(cause, trace, m) ++
      Option(cause.getCause).map(toString(_, trace)).getOrElse(Nil)
  }

  private def enroll(
      ex: Throwable,
      trace: Array[StackTraceElement],
      max: Int): List[String] = {
    val builder = new StringBuilder
    builder.append(HEAD_MARK).append(ex.getClass.getName).append(SEPARATOR)
    builder.append(ex.getMessage).append(SEPARATOR)
    builder.append(trace.length).append(SEPARATOR).append(max)
    List(builder.toString) ++ (0 to max).map { i =>
      builder.setLength(0)
      builder.append(trace(i).getClassName).append(SEPARATOR)
      builder.append(trace(i).getMethodName).append(SEPARATOR)
      builder.append(Option(trace(i).getFileName).getOrElse("")).append(SEPARATOR)
      builder.append(trace(i).getLineNumber)
      builder.toString
    }.toList
  }
  private def newInstance(className: String, message: String, cause: Throwable): Throwable = {
    try {
      DynConstructors.builder()
        .impl(className, classOf[String], classOf[Throwable])
        .buildChecked[Throwable]()
        .newInstance(message, cause)
    } catch {
      case _: Exception => new RuntimeException(className + ":" + message, cause)
    }
  }

  private def getCoordinates(line: String): (Int, Int, Int) = {
    val i1 = line.indexOf(SEPARATOR)
    val i3 = line.lastIndexOf(SEPARATOR)
    val i2 = line.substring(0, i3).lastIndexOf(SEPARATOR)
    (i1, i2, i3)
  }

  def toCause(details: Iterable[String]): Throwable = {
    var ex: Throwable = null
    if (details != null && details.nonEmpty) {
      val head = details.head
      val (i1, i2, i3) = getCoordinates(head)
      val exClz = head.substring(1, i1)
      val msg = head.substring(i1 + 1, i2)
      val length = head.substring(i3 + 1).toInt
      val stackTraceElements = details.tail.take(length + 1).map { line =>
        val (i1, i2, i3) = getCoordinates(line)
        val clzName = line.substring(0, i1)
        val methodName = line.substring(i1 + 1, i2)
        val fileName = line.substring(i2 + 1, i3)
        val lineNum = line.substring(i3 + 1).toInt
        new StackTraceElement(clzName, methodName, fileName, lineNum)
      }
      ex = newInstance(exClz, msg, toCause(details.slice(length + 2, details.size)))
      ex.setStackTrace(stackTraceElements.toArray)
    }
    ex
  }

  @tailrec
  def findCause(t: Throwable): Throwable = t match {
    case e @ (_: UndeclaredThrowableException | _: InvocationTargetException)
        if e.getCause != null => findCause(e.getCause)
    case e => e
  }
}
