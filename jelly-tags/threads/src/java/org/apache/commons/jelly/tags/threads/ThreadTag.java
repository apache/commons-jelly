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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.NestedRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A tag that spawns the contained script in a separate thread.  A thread
 * can wait on another thread or another thread group to finish before starting.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
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
    /** Set if the thread should be a daemon or not */
    private boolean daemon = false;
    /** the destination of output */
    private XMLOutput xmlOutput;
    /** Should we close the underlying output */
    private boolean closeOutput;
    /** Should a new context be created */
    private boolean newContext = false;
    /** Keep a reference to the thread */
    private JellyThread thread = new JellyThread();

    public ThreadTag() {
        super();
    }

    public ThreadTag(boolean shouldTrim) {
        super(shouldTrim);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(final XMLOutput output) throws Exception {
        if (xmlOutput == null) {
            // lets default to system.out
            xmlOutput = XMLOutput.createXMLOutput(System.out);
        }

        // lets create a child context
        final JellyContext useThisContext = newContext ? context.newJellyContext() : context;

        // set the target to run
        thread.setTarget(new Runnable() {
            public void run() {
                try {
                    getBody().run(useThisContext, xmlOutput);
                    if (closeOutput) {
                        xmlOutput.close();
                    } 
                    else {
                        xmlOutput.flush();
                    }
                } 
                catch (JellyException e) {
                    // jelly wraps the exceptions thrown
                    Throwable subException = e.getCause();
                    if (subException != null) {
                        if (subException instanceof TimeoutException) {
                            throw (TimeoutException)subException;
                        } else if (subException instanceof RequirementException) {
                            throw (RequirementException)subException;
                        }
                    }

                    log.error(e);

                    // wrap the exception with a RuntimeException
                    throw new NestedRuntimeException(e);
                }
                catch (Exception e) {
                    log.error(e);

                    // wrap the exception with a RuntimeException
                    if (e instanceof RuntimeException) {
                    	throw (RuntimeException) e;
                    }
                    else {
                    	throw new NestedRuntimeException(e);
                    }
                }
            }
        });

        // set the threads priority
        thread.setPriority(priority);

        // set the threads name
        if (name != null) {
            thread.setName(name);
        } else {
            thread.setName("Jelly Thread #" + (threadNumber++));
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
        GroupTag gt = (GroupTag) findAncestorWithClass(GroupTag.class);
        if (gt != null) {
            gt.addThread(thread);
        } else {
            // start the thread
            thread.start();
        }
    }

    /**
     * Sets the variable name to export, optional
     * @param var The variable name
     */
    public void setVar(String var) {
        this.var = var;
        if (name == null) {
            name = var;
        }
    }

    /**
     * Sets the name of the thread.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the threads priority. Defaults to Thread.NORM_PRIORITY
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Sets the thread to be a daemon thread if true
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * Sets the destination of output
     */
    public void setXmlOutput(XMLOutput xmlOutput) {
        this.closeOutput = false;
        this.xmlOutput = xmlOutput;
    }

    /**
     * Set the file which is generated from the output
     * @param name The output file name
     */
    public void setFile(String name) throws IOException {
        this.closeOutput = true;
        setXmlOutput(XMLOutput.createXMLOutput(new FileOutputStream(name)));
    }

    /**
     * Should a new context be created for this thread?
     */
    public void setNewContext(boolean newContext) {
        this.newContext = newContext;
    }

    /**
     * Get the thread instance
     * @return The thread
     */
    public Thread getThread() {
        return thread;
    }
}
