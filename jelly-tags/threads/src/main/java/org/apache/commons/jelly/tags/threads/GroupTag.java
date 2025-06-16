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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Represents a group of threads. This is not the same as Java's thread groups.
 * All of the threads in a thread group are started at the same time, not as they
 * are defined. Use this in conjunction with other tags like join to manipulate
 * a group of threads.
 */

public class GroupTag extends TagSupport {
    /** Variable to place the thread group into */
    private String var = null;
    /** The thread list */
    private final List threads = new ArrayList();

    /** Add a thread to the thread group list */
    public void addThread(final Thread thread) {
        threads.add(thread);
    }

    /** Child threads will add themselves and will then all be started together */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);

        // store the group in a jelly variable
        if (var != null) {
            context.setVariable(var, threads);
        }

        // start the threads
        for (final Object thread2 : threads) {
            final Thread thread = (Thread) thread2;
            thread.start();
        }
    }

    /** Gets the list of threads in this thread group */
    public List getThreads() {
        return threads;
    }

    /** Sets the variable name to store the thread group in */
    public void setVar(final String var) {
        this.var = var;
    }
}
