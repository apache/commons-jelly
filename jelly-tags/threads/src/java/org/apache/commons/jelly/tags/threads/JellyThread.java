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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Adds some functionality to the jdk thread class.
 *
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
 */

public class JellyThread extends Thread {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ThreadTag.class);

    /** While this thread is still running it owns this mutex */
    private Mutex runningMutex = new Mutex();
    /** The Runnable target */
    private Runnable target = null;

    /** Tracks the status of this thread */
    RunnableStatus status = new RunnableStatus();

    public JellyThread() {
        // aquire my still running lock immediately
        while (true) {
            try {
                runningMutex.acquire();
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Set the Runnable target that will be run
     */
    public void setTarget(Runnable target) {
        this.target = target;
    }

    /**
     * Run the thread
     */
    public void run() {
        log.debug("Starting thread \"" + getName() + "\"");

        // run the runnable item
        try {

            log.debug("Thread \"" + getName() + "\" running");
            target.run();

            // as long as there were no runtime exceptions set SUCCESS
            status.set(RunnableStatus.SUCCESS);

        } catch(RequirementException e) {

            status.set(RunnableStatus.AVOIDED);
            log.warn("Thread \"" + getName() + "\" avoided, " + e.getMessage());

        } catch(TimeoutException e) {

            status.set(RunnableStatus.AVOIDED);
            log.warn("Thread \"" + getName() + "\" avoided, " + e.getMessage());

        } catch (Exception e) {

            // runtime exceptions will cause a status of FAILURE
            status.set(RunnableStatus.FAILURE, e);
            log.error("Thread \"" + getName() + "\" failure, " + e.getMessage());
            log.debug(e);

        }

        // release the i'm still running mutex
        runningMutex.release();

        log.debug("Thread \"" + getName() + "\" finished");
    }

    /**
     * Call this method from a different thread to wait until this thread is done. This
     * is used by the {@link WaitForTag} class.
     */
    public void waitUntilDone(long howLong) throws TimeoutException {
        if (Thread.currentThread() == this) {
            throw new RuntimeException("This method should be called from a different thread than itself");
        }

        // wait until the calling thread can aquire the lock
        while (true) {
            try {
                if (howLong == -1) {
                    runningMutex.acquire();
                    break;
                } else if (!runningMutex.attempt(howLong)) {
                    throw new TimeoutException("max wait time exceeded");
                }
            } catch (InterruptedException e) {
            }
        }

        // release the lock, just needed it to get started
        runningMutex.release();
    }

    /** Get the status of this thread */
    public RunnableStatus getStatus() {
        return status;
    }
}
