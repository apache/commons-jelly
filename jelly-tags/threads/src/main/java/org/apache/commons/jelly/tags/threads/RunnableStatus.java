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

package org.apache.commons.jelly.tags.threads;

/**
 * Represents the status of {@link JellyThread}.
 */

public class RunnableStatus {
    public static final int NONE = 0;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int AVOIDED = 3;
    public static final int TIMED_OUT = 4;
    public static final int KILLED = 5;

    /**
     * Used to get the status code from a string representation. Mainly used for
     * XML parsing.
     * @param status The status string rep.
     * @return The status enum value
     */
    public static int getStatusCode(final String status) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            return SUCCESS;
        }
        if (status.equalsIgnoreCase("FAILURE")) {
            return FAILURE;
        }
        if (status.equalsIgnoreCase("TIMED_OUT")) {
            return TIMED_OUT;
        }
        if (status.equalsIgnoreCase("AVOIDED")) {
            return AVOIDED;
        }
        if (status.equalsIgnoreCase("KILLED")) {
            return KILLED;
        }
        throw new IllegalArgumentException(status + " is invalid status");
    }

    /**
     * The reverse of getStatusCode
     */
    public static String getStatusString(final int status) {
        switch (status) {
        case SUCCESS:
            return "SUCCESS";
        case FAILURE:
            return "FAILURE";
        case TIMED_OUT:
            return "TIMED_OUT";
        case AVOIDED:
            return "AVOIDED";
        case KILLED:
            return "KILLED";
        default:
            throw new IllegalArgumentException(status + " is invalid status");
        }
    }

    public static boolean isValidStatus(final int status) {
        switch (status) {
        case SUCCESS:
            return true;
        case FAILURE:
            return true;
        case TIMED_OUT:
            return true;
        case AVOIDED:
            return true;
        case KILLED:
            return true;
        default:
            return false;
        }
    }

    private int status = NONE;

    /** On a status change to FAILURE an exception can be set */
    private Exception exception = null;

    public RunnableStatus() {

    }

    public RunnableStatus(final int status) {
        set(status);
    }

    public synchronized boolean equals(final int status) {
        return this.status == status;
    }

    public synchronized boolean equals(final RunnableStatus status) {
        return status.get() == this.status;
    }

    public synchronized int get() {
        return status;
    }

    public synchronized Exception getException() {
        return exception;
    }

    public synchronized boolean isAvoided() {
        return status == AVOIDED;
    }

    public synchronized boolean isFailure() {
        return status == FAILURE;
    }

    public synchronized boolean isKilled() {
        return status == KILLED;
    }

    public synchronized boolean isSuccess() {
        return status == SUCCESS;
    }

    public synchronized boolean isTimedOut() {
        return status == TIMED_OUT;
    }

    public synchronized void set(final int status) {
        set(status, null);
    }

    public synchronized void set(final int status, final Exception e) {
        // this check is important since I may call setStatus(BLAH) again
        // to trigger the callback
        if (this.status != status) {
            this.status = status;

            // store the exception if one was set
            if (e != null) {
                this.exception = e;
            }
        }
    }

    @Override
    public String toString() {
        return getStatusString(status);
    }
}