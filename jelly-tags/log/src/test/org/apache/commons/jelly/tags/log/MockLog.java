/*
 * /home/cvs/jakarta-commons/jelly/jelly-tags/log/src/test/org/apache/commons/jelly/tags/log/MockLog.java,v 1.1 2003/01/22 10:22:30 jstrachan Exp
 * 1.1
 * 2003/01/22 10:22:30
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * MockLog.java,v 1.1 2003/01/22 10:22:30 jstrachan Exp
 */
package org.apache.commons.jelly.tags.log;

import org.apache.commons.logging.Log;

/**
 * A Mock Object useful for unit testing of commons-logging. (Maybe this should
 * be contributed back to commons-logging?)
 * 
 * @author James Strachan
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
     * @see org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object message, Throwable exception) {
        this.debug = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    public void debug(Object message) {
        this.debug = message;
    }

    /**
     * @see org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
     */
    public void error(Object message, Throwable exception) {
        this.error = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    public void error(Object message) {
        this.error = message;
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
     */
    public void fatal(Object message, Throwable exception) {
        this.fatal = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    public void fatal(Object message) {
        this.fatal = message;
    }

    /**
     * @see org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
     */
    public void info(Object message, Throwable exception) {
        this.info = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    public void info(Object message) {
        this.info = message;
    }

    /**
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    public boolean isFatalEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    public boolean isTraceEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * @see org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
     */
    public void trace(Object message, Throwable exception) {
        this.trace = message;
        this.lastThrowable = exception;
    }

    /**
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    public void trace(Object message) {
        this.trace = message;
    }

    /**
     * @see org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
     */
    public void warn(Object message, Throwable exception) {
        this.warn = message;
    }

    /**
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    public void warn(Object message) {
        this.warn = message;
    }

    // Properties
    //-------------------------------------------------------------------------

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
     * Returns the warn.
     * @return Object
     */
    public Object getWarn() {
        return warn;
    }

    /**
     * Returns the trace.
     * @return Object
     */
    public Object getTrace() {
        return trace;
    }

    /**
     * Returns the debug.
     * @return Object
     */
    public Object getDebug() {
        return debug;
    }

}
