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
 */

package org.apache.commons.jelly.tags.threads;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.util.List;

/**
 * This tag creates a dependency on another thread. If onlyWait is set
 * a {@link TimeoutException} can be thrown. If status is set a {@link RequirementException}
 * can be thrown.
 *
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
 */

public class WaitForTag extends TagSupport {
    private int status = RunnableStatus.NONE;
    private JellyThread thread = null;
    private List group = null;
    private long onlyWait = -1;

    /**
     * Wait for a specific status. "SUCCESS", "FAILURE", "TIMED_OUT", or "AVOIDED". If
     * waiting on a thread group each thread in the group will have to have this status
     * set.
     */
    public void setStatus(String status) {
        this.status = RunnableStatus.getStatusCode(status);
    }

    /**
     * Which thread will this tag check the status of
     */
    public void setThread(JellyThread thread) {
        this.thread = thread;
    }

    /**
     * Set the group of threads to wait on
     */
    public void setGroup(List group) {
        this.group = group;
    }

    /**
     * Set how long to wait for the thread to finish. If waiting for a group
     * this will be the time to wait for each thread in the group to finish.
     */
    public void setOnlyWait(long onlyWait) {
        this.onlyWait = onlyWait;
    }

    /**
     * Check the requirements
     * @throws TimeoutException If the call to waitUntilDone(onlyWait) times out
     * @throws RequirementException If a threads status doesn't match the setStatus() value
     */
    public void doTag(XMLOutput output) throws TimeoutException, RequirementException, JellyTagException {
        if (thread == null && group == null) {
            throw new JellyTagException("This tag requires that you set the thread or group attribute");
        }

        // wait on the thread
        if (thread != null) {
            thread.waitUntilDone(onlyWait);
            if (status != RunnableStatus.NONE) {
                if (!thread.getStatus().equals(status)) {
                    throw new RequirementException("Requirement on thread \"" + thread.getName() + "\" not met");
                }
            }
        }

        // wait on the threadgroup
        if (group != null) {
            for (int i = 0; i < group.size(); i++) {
                JellyThread gthread = (JellyThread) group.get(i);
                gthread.waitUntilDone(onlyWait);
                if (status != RunnableStatus.NONE) {
                    if (!gthread.getStatus().equals(status)) {
                        throw new RequirementException("Requirement on thread \"" + gthread.getName() + "\" not met");
                    }
                }
            }
        }
    }
}
