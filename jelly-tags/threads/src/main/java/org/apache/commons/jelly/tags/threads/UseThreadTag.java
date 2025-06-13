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
 * Base class for tags that will "use" threads.
 */

public abstract class UseThreadTag extends TagSupport {
    /** The thread to use in some way. */
    private Thread thread = null;
    /** Threads can be grouped and acted on as a set */
    private List threadGroup = null;
    /** If true doTag will search for a parent thread to use if setThread was not called */
    private boolean searchForParent = true;

    /**
     * The default behavior is to either use the set thread or to
     * search for a parent thread to use.
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            // either use the set thread or search for a parent thread to use
            if (thread != null) {
                useThread(thread, output);
            } else if (threadGroup != null) {
                useThreadGroup(threadGroup, output);
            } else // check if this tag is nested inside a thread. if so
            // use the parent thread.
            if (searchForParent) {
                // first look for parent threads
                final ThreadTag tt = (ThreadTag) findAncestorWithClass(ThreadTag.class);

                if (tt != null) {
                    useThread(tt.getThread(), output);
                } else {
                    // then look for parent thread groups
                    final GroupTag gt = (GroupTag) findAncestorWithClass(GroupTag.class);
                    if (gt == null) {
                        throw new JellyTagException("no thread or thread group found");
                    }
                    useThreadGroup(gt.getThreads(), output);
                }
            } else {
                throw new JellyTagException("no thread or thread group found");
            }
        }
        catch (final InterruptedException e) {
            throw new JellyTagException(e);
        }
    }

    /** Implement this method to do something with the thread */
    protected abstract void useThread(Thread thread, XMLOutput output) throws InterruptedException ;

    /** Implement this method to do something with the threadGroup */
    protected abstract void useThreadGroup(List threadGroup, XMLOutput output) throws InterruptedException ;

    /**
     * Sets the thread to use in some way.
     */
    public void setThread(final Thread thread) {
        this.thread = thread;
    }

    /**
     * Gets a reference to the thread to use
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Sets the thread group to "use".
     * @param threadGroup The threadGroup created with the <em>group</em> tag.
     */
    public void setThreadGroup(final List threadGroup) {
        this.threadGroup = threadGroup;
    }

    /**
     * Gets the thread group
     */
    public List getThreadGroup() {
        return threadGroup;
    }

    /**
     * If true the tag will search for a parent thread tag to "use" if
     * no thread was set via <em>setThread</em>. This is <em>true</em> by default.
     */
    public void setSearchForParentThread(final boolean searchForParent) {
        this.searchForParent = searchForParent;
    }
}
