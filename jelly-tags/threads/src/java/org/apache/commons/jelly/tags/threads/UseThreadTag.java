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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.util.List;

/**
 * Base class for tags that will "use" threads.
 *
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
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
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            // either use the set thread or search for a parent thread to use
            if (thread != null) {
                useThread(thread, output);
            } else if (threadGroup != null) {
                useThreadGroup(threadGroup, output);
            } else {
                // check if this tag is nested inside a thread. if so
                // use the parent thread.
                if (searchForParent) {
                    // first look for parent threads
                    ThreadTag tt = (ThreadTag) findAncestorWithClass(ThreadTag.class);        

                    if (tt != null) {
                        useThread(tt.getThread(), output);
                    } else {
                        // then look for parent thread groups
                        GroupTag gt = (GroupTag) findAncestorWithClass(GroupTag.class);
                        if (gt != null) {
                            useThreadGroup(gt.getThreads(), output);
                        } else {
                            throw new JellyTagException("no thread or thread group found");
                        }
                    }
                } else {
                    throw new JellyTagException("no thread or thread group found");
                }
            }
        }
        catch (InterruptedException e) {
            throw new JellyTagException(e);
        }
    }

    /** Implement this method to do something with the thread */
    protected abstract void useThread(Thread thread, XMLOutput output) throws InterruptedException ;

    /** Implement this method to do something with the threadGroup */
    protected abstract void useThreadGroup(List threadGroup, XMLOutput output) throws InterruptedException ;

    /**
     * Set the thread to use in some way.
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * Get a reference to the thread to use
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Set the thread group to "use".
     * @param threadGroup The threadGroup created with the <i>group</i> tag.
     */
    public void setThreadGroup(List threadGroup) {
        this.threadGroup = threadGroup;
    }

    /**
     * Get the thread group
     */
    public List getThreadGroup() {
        return threadGroup;
    }

    /**
     * If true the tag will search for a parent thread tag to "use" if
     * no thread was set via <i>setThread</i>. This is <i>true</i> by default.
     */
    public void setSearchForParentThread(boolean searchForParent) {
        this.searchForParent = searchForParent;
    }
}
