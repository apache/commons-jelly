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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.NestedRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag that spawns the contained script in a separate thread.  A thread
 * can wait on another thread or another thread group to finish before starting.
 */
public class ThreadTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ThreadTag.class);

    /** The current thread number. Used for default thread naming */
    private static int threadNumber = 0;

    /** Variable to place the thread into */
    private String var = null;

    /** Thread Name */
    private String name = null;

    /** Thread priority, defaults to Thread.NORM_PRIORITY */
    private int priority = Thread.NORM_PRIORITY;

    /** Sets if the thread should be a daemon or not */
    private boolean daemon = false;

    /** The destination of output */
    private XMLOutput xmlOutput;

    /** Should we close the underlying output */
    private boolean closeOutput;

    /** Should a new context be created */
    private boolean newContext = false;

    /** Keep a reference to the thread */
    private final JellyThread thread = new JellyThread();

    public ThreadTag() {
    }

    public ThreadTag(final boolean shouldTrim) {
        super(shouldTrim);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (xmlOutput == null) {
            // lets default to system.out
            try {
                xmlOutput = XMLOutput.createXMLOutput(System.out);
            }
            catch (final UnsupportedEncodingException e) {
                throw new JellyTagException(e);
            }
        }

        // lets create a child context
        final JellyContext useThisContext = newContext ? context.newJellyContext() : context;

        // set the target to run
        thread.setTarget(() -> {
            try {
                getBody().run(useThisContext, xmlOutput);
                if (closeOutput) {
                    xmlOutput.close();
                }
                else {
                    xmlOutput.flush();
                }
            }
            catch (final JellyTagException e) {
                // jelly wraps the exceptions thrown
                final Throwable subException = e.getCause();
                if (subException != null) {
                    if (subException instanceof TimeoutException) {
                        throw (TimeoutException)subException;
                    }
                    if (subException instanceof RequirementException) {
                        throw (RequirementException)subException;
                    }
                }

                log.error(e);

                // wrap the exception with a RuntimeException
                throw new NestedRuntimeException(e);
            }
            catch (final Exception e) {
                log.error(e);

                // wrap the exception with a RuntimeException
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new NestedRuntimeException(e);
            }
        });

        // set the threads priority
        thread.setPriority(priority);

        // set the threads name
        if (name != null) {
            thread.setName(name);
        } else {
            thread.setName("Jelly Thread #" + threadNumber++);
        }

        // set whether this thread is a daemon thread
        thread.setDaemon(daemon);

        // save the thread in a context variable
        if (var != null) {
            context.setVariable(var, thread);
        }

        // check if this tag is nested inside a group tag. if so
        // add this thread to the thread group but do not start it.
        // all threads in a thread group should start together.
        final GroupTag gt = (GroupTag) findAncestorWithClass(GroupTag.class);
        if (gt != null) {
            gt.addThread(thread);
        } else {
            // start the thread
            thread.start();
        }
    }

    /**
     * Gets the thread instance
     * @return The thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Sets the thread to be a daemon thread if true
     */
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * Sets the file which is generated from the output
     * @param name The output file name
     */
    public void setFile(final String name) throws IOException {
        this.closeOutput = true;
        setXmlOutput(XMLOutput.createXMLOutput(new FileOutputStream(name)));
    }

    /**
     * Sets the name of the thread.
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Should a new context be created for this thread?
     */
    public void setNewContext(final boolean newContext) {
        this.newContext = newContext;
    }

    /**
     * Sets the threads priority. Defaults to Thread.NORM_PRIORITY
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Sets the variable name to export, optional
     * @param var The variable name
     */
    public void setVar(final String var) {
        this.var = var;
        if (name == null) {
            name = var;
        }
    }

    /**
     * Sets the destination of output
     */
    public void setXmlOutput(final XMLOutput xmlOutput) {
        this.closeOutput = false;
        this.xmlOutput = xmlOutput;
    }
}
