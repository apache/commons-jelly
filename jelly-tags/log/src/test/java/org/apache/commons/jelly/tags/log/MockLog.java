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
package org.apache.commons.jelly.tags.log;

import org.apache.commons.logging.Log;

/**
 * A Mock Object useful for unit testing of commons-logging. (Maybe this should
 * be contributed back to commons-logging?)
 * @version 1.1 2003/01/22 10:22:30
 */
public class MockLog implements Log {

    private Object debug;
    private Object trace;
    private Object info;
    private Object warn;
    private Object error;
    private Object fatal;
    private Throwable lastThrowable;

    public MockLog() {
    }

    /**
     * Resets all the last logging messages received
     */
    public void clear() {
        this.debug = null;
        this.trace = null;
        this.info = null;
        this.warn = null;
        this.error = null;
        this.fatal = null;
        this.lastThrowable = null;
    }

    // Log interface
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    @Override
    public void debug(final Object message) {
        this.debug = message;
    }

    /**
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    @Override
    public void debug(final Object message, final Throwable exception) {
        this.debug = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    @Override
    public void error(final Object message) {
        this.error = message;
    }

    /**
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    @Override
    public void error(final Object message, final Throwable exception) {
        this.error = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    @Override
    public void fatal(final Object message) {
        this.fatal = message;
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    @Override
    public void fatal(final Object message, final Throwable exception) {
        this.fatal = message;
        this.lastThrowable = exception;
    }

    /**
     * Returns the debug.
     * @return Object
     */
    public Object getDebug() {
        return debug;
    }

    /**
     * Returns the error.
     * @return Object
     */
    public Object getError() {
        return error;
    }

    /**
     * Returns the fatal.
     * @return Object
     */
    public Object getFatal() {
        return fatal;
    }

    /**
     * Returns the info.
     * @return Object
     */
    public Object getInfo() {
        return info;
    }

    /**
     * Returns the lastThrowable.
     * @return Throwable
     */
    public Throwable getLastThrowable() {
        return lastThrowable;
    }

    /**
     * Returns the trace.
     * @return Object
     */
    public Object getTrace() {
        return trace;
    }

    /**
     * Returns the warn.
     * @return Object
     */
    public Object getWarn() {
        return warn;
    }

    /**
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    @Override
    public void info(final Object message) {
        this.info = message;
    }

    /**
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    @Override
    public void info(final Object message, final Throwable exception) {
        this.info = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    @Override
    public void trace(final Object message) {
        this.trace = message;
    }

    /**
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    @Override
    public void trace(final Object message, final Throwable exception) {
        this.trace = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    @Override
    public void warn(final Object message) {
        this.warn = message;
    }

    /**
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    @Override
    public void warn(final Object message, final Throwable exception) {
        this.warn = message;
    }

}
