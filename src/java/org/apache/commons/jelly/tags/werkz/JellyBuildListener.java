/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/werkz/Attic/JellyBuildListener.java,v 1.5 2002/12/11 12:40:53 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/12/11 12:40:53 $
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
 *
 * $Id: JellyBuildListener.java,v 1.5 2002/12/11 12:40:53 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.werkz;

import java.io.IOException;
import java.util.Stack;

import org.apache.commons.jelly.XMLOutput;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.xml.sax.SAXException;

public class JellyBuildListener implements BuildListener
{
    private XMLOutput out;

    private Stack taskStack;

    private boolean debug;

    /** Whether or not to use emacs-style output */
    protected boolean emacsMode = false;

    public JellyBuildListener(XMLOutput out) {
        this.taskStack = new Stack();
        this.out       = out;
        this.debug     = false;
    }

    /**
     * Sets this logger to produce emacs (and other editor) friendly output.
     *
     * @param emacsMode <code>true</code> if output is to be unadorned so that
     *                  emacs and other editors can parse files names, etc.
     */
    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void isDebug(boolean debug) {
        this.debug = debug;
    }

    public void buildFinished(BuildEvent event) {
    }

    public void buildStarted(BuildEvent event) {
    }

    public void messageLogged(BuildEvent event) {

        // System.err.println( "messageLogged(" + event + ")" );

        if ( event.getPriority() > Project.MSG_INFO
             &&
             ! isDebug() ) {
            return;
        }

        try {
            if ( emacsMode ) {
                out.write( event.getMessage() + "\n" );
                out.flush();
                return;
            }
            
            if ( ! this.taskStack.isEmpty() ) {
                out.write( "    [" + this.taskStack.peek() + "] " );
            }

            switch ( event.getPriority() ) {
                case ( Project.MSG_ERR ): {
                    out.write( "[ERROR] ");
                    break;
                }
                case ( Project.MSG_WARN ): {
                    // out.write( "[WARN] ");
                    break;
                }
                case ( Project.MSG_INFO ): {
                    // normal, do nothing.
                    break;
                }
                case ( Project.MSG_VERBOSE ): {
                    out.write( "[VERBOSE] ");
                    break;
                }
                case ( Project.MSG_DEBUG ): {
                    out.write( "[DEBUG] ");
                    break;
                }
            }
            
            out.write( event.getMessage() + "\n" );
            out.flush();
        } catch (SAXException e) {
            // fall-back to stderr.
            System.err.println( event.getMessage() );
            System.err.flush();
        } catch (IOException e) {
            // ignore
        }
            
    }

    public void targetFinished(BuildEvent event) {
    }

    public void targetStarted(BuildEvent event) {
    }

    public void taskFinished(BuildEvent event) {
        this.taskStack.pop();
    }

    public void taskStarted(BuildEvent event) {
        this.taskStack.push( event.getTask().getTaskName() );
    }
}


