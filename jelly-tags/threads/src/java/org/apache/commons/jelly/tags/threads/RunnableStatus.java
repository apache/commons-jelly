/*
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 */

package org.apache.commons.jelly.tags.threads;

/**
 * Represents the status of {@link JellyThread}.
 *
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
 */

public class RunnableStatus {
    public static final int NONE = 0;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int AVOIDED = 3;
    public static final int TIMED_OUT = 4;
    public static final int KILLED = 5;

    private int status = NONE;

    /** On a status change to FAILURE an exception can be set */
    private Exception exception = null;

    public RunnableStatus() {

    }

    public RunnableStatus(int status) {
        set(status);
    }

    public synchronized void set(int status) {
        set(status, null);
    }

    public synchronized void set(int status, Exception e) {
        // this check is important since I may call setStatus(BLAH) again
        // to trigger the callback
        if (this.status != status) {
            this.status = status;

            // store the exception if one was set
            if (e != null)
                this.exception = e;
        }
    }

    public synchronized int get() {
        return status;
    }

    public synchronized boolean isSuccess() {
        return (status == SUCCESS);
    }

    public synchronized boolean isFailure() {
        return (status == FAILURE);
    }

    public synchronized boolean isAvoided() {
        return (status == AVOIDED);
    }

    public synchronized boolean isTimedOut() {
        return (status == TIMED_OUT);
    }

    public synchronized boolean isKilled() {
        return (status == KILLED);
    }

    public synchronized Exception getException() {
        return exception;
    }

    public synchronized boolean equals(RunnableStatus status) {
        return status.get() == this.status;
    }

    public synchronized boolean equals(int status) {
        return this.status == status;
    }

    /**
     * Used to get the status code from a string representation. Mainly used for
     * xml parsing.
     * @param status The status string rep.
     * @return The status enum value
     */
    public static int getStatusCode(String status) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            return SUCCESS;
        } else if (status.equalsIgnoreCase("FAILURE")) {
            return FAILURE;
        } else if (status.equalsIgnoreCase("TIMED_OUT")) {
            return TIMED_OUT;
        } else if (status.equalsIgnoreCase("AVOIDED")) {
            return AVOIDED;
        } else if (status.equalsIgnoreCase("KILLED")) {
            return KILLED;
        } else {
            throw new IllegalArgumentException(status + " is invalid status");
        }
    }

    /**
     * The reverse of getStatusCode
     */
    public static String getStatusString(int status) {
        if (status == SUCCESS) {
            return "SUCCESS";
        } else if (status == FAILURE) {
            return "FAILURE";
        } else if (status == TIMED_OUT) {
            return "TIMED_OUT";
        } else if (status == AVOIDED) {
            return "AVOIDED";
        } else if (status == KILLED) {
            return "KILLED";
        } else {
            throw new IllegalArgumentException(status + " is invalid status");
        }
    }

    public static boolean isValidStatus(int status) {
        if (status == SUCCESS) {
            return true;
        } else if (status == FAILURE) {
            return true;
        } else if (status == TIMED_OUT) {
            return true;
        } else if (status == AVOIDED) {
            return true;
        } else if (status == KILLED) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return getStatusString(status);
    }
}