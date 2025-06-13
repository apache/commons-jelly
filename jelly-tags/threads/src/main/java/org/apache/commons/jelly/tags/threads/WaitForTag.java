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

import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * This tag creates a dependency on another thread. If onlyWait is set
 * a {@link TimeoutException} can be thrown. If status is set a {@link RequirementException}
 * can be thrown.
 */

public class WaitForTag extends TagSupport {
    private int status = RunnableStatus.NONE;
    private JellyThread thread = null;
    private List group = null;
    private long onlyWait = -1;

    /**
     * Check the requirements
     * @throws TimeoutException If the call to waitUntilDone(onlyWait) times out
     * @throws RequirementException If a threads status doesn't match the setStatus() value
     */
    @Override
    public void doTag(final XMLOutput output) throws TimeoutException, RequirementException, JellyTagException {
        if (thread == null && group == null) {
            throw new JellyTagException("This tag requires that you set the thread or group attribute");
        }

        // wait on the thread
        if (thread != null) {
            thread.waitUntilDone(onlyWait);
            if ((status != RunnableStatus.NONE) && !thread.getStatus().equals(status)) {
                throw new RequirementException("Requirement on thread \"" + thread.getName() + "\" not met");
            }
        }

        // wait on the threadgroup
        if (group != null) {
            for (final Object element : group) {
                final JellyThread gthread = (JellyThread) element;
                gthread.waitUntilDone(onlyWait);
                if ((status != RunnableStatus.NONE) && !gthread.getStatus().equals(status)) {
                    throw new RequirementException("Requirement on thread \"" + gthread.getName() + "\" not met");
                }
            }
        }
    }

    /**
     * Sets the group of threads to wait on
     */
    public void setGroup(final List group) {
        this.group = group;
    }

    /**
     * Sets how long to wait for the thread to finish. If waiting for a group
     * this will be the time to wait for each thread in the group to finish.
     */
    public void setOnlyWait(final long onlyWait) {
        this.onlyWait = onlyWait;
    }

    /**
     * Wait for a specific status. "SUCCESS", "FAILURE", "TIMED_OUT", or "AVOIDED". If
     * waiting on a thread group each thread in the group will have to have this status
     * set.
     */
    public void setStatus(final String status) {
        this.status = RunnableStatus.getStatusCode(status);
    }

    /**
     * Which thread will this tag check the status of
     */
    public void setThread(final JellyThread thread) {
        this.thread = thread;
    }
}
