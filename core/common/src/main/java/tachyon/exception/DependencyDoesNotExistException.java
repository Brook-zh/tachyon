/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.exception;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The excepton thrown when a dependency does not exist in Tachyon.
 */
@ThreadSafe
public class DependencyDoesNotExistException extends TachyonException {
  private static final long serialVersionUID = -8411287237224325573L;

  private static final TachyonExceptionType EXCEPTION_TYPE =
      TachyonExceptionType.DEPENDENCY_DOES_NOT_EXIST;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public DependencyDoesNotExistException(String message) {
    super(EXCEPTION_TYPE, message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public DependencyDoesNotExistException(String message, Throwable cause) {
    super(EXCEPTION_TYPE, message, cause);
  }

  /**
   * Constructs a new exception with the specified exception message and multiple parameters.
   *
   * @param message the exception message
   * @param params the parameters
   */
  public DependencyDoesNotExistException(ExceptionMessage message, Object... params) {
    this(message.getMessage(params));
  }

  /**
   * Constructs a new exception with the specified exception message, the cause and multiple
   * parameters.
   *
   * @param message the exception message
   * @param cause the cause
   * @param params the parameters
   */
  public DependencyDoesNotExistException(ExceptionMessage message, Throwable cause,
      Object... params) {
    this(message.getMessage(params), cause);
  }
}
