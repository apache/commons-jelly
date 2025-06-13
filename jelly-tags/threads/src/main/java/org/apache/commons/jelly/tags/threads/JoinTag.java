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

import org.apache.commons.jelly.XMLOutput;

/**
 * A thread join waits until a thread or threadGroup is complete.
 */

public class JoinTag extends UseThreadTag {
    /** How long to wait */
    private long timeout = -1;

    /** Perform the thread join */
    @Override
    protected void useThread(final Thread thread, final XMLOutput output) throws InterruptedException {
        joinThread(thread);
    }

    /** Join all of the threads in a thread group */
    @Override
    protected void useThreadGroup(final List threadGroup, final XMLOutput output) throws InterruptedException {
        for (final Object element : threadGroup) {
            joinThread((Thread) element);
        }
    }

    /** Join a thread */
    private void joinThread(final Thread thread) throws InterruptedException {
        if (timeout > 0) {
            thread.join(timeout);
        } else {
            thread.join();
        }
    }

    /**
     * How long should the join wait. If &lt;= 0 the join waits until the
     * thread is dead.
     * @param timeout in millis
     */
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
}
