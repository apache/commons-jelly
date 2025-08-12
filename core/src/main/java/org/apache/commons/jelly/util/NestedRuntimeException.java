/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.util;

/**
 * A {@link RuntimeException} which is nested to preserve stack traces.
 * <p>
 * This class allows the following code to be written to convert a regular
 * Exception into a {@link RuntimeException} without losing the stack trace.
 * </p>
 * <pre>
 *    try {
 *        ...
 *    } catch (Exception e) {
 *        throw new RuntimeException(e);
 *    }
 * </pre>
 * @deprecated Use {@link RuntimeException}.
 */
@Deprecated
public class NestedRuntimeException extends RuntimeException {

    /**
     * Constructs a new {@code NestedRuntimeException} with specified
     * detail message and nested {@code Throwable}.
     *
     * @param msg    the error message
     * @param cause  the exception or error that caused this exception to be
     * thrown
     */
    public NestedRuntimeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new {@code NestedRuntimeException} with specified
     * nested {@code Throwable}.
     *
     * @param cause the exception or error that caused this exception to be
     * thrown
     */
    public NestedRuntimeException(final Throwable cause) {
        super(cause);
    }

}
