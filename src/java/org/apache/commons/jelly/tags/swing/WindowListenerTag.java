/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Creates a WindowListener which is attached to its parent window control which will invoke
 * named Jelly scripts as window events are fired, or will invoke its body if there is no script
 * specified for the named event type.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class WindowListenerTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(WindowListenerTag.class);

    private String var;
    private Script activated;
    private Script closed;
    private Script closing;
    private Script deactivated;
    private Script deiconified;
    private Script iconified;
    private Script opened;

    public WindowListenerTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws Exception {

        // now lets add this action to its parent if we have one
        ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag != null ) {
            WindowListener listener = new WindowListener() {
                public void windowActivated(WindowEvent e) {
                    invokeScript( output, e, activated );
                }

                public void windowClosed(WindowEvent e) {
                    invokeScript( output, e, closed );
                }

                public void windowClosing(WindowEvent e) {
                    invokeScript( output, e, closing );
                }

                public void windowDeactivated(WindowEvent e) {
                    invokeScript( output, e, deactivated );
                }

                public void windowDeiconified(WindowEvent e) {
                    invokeScript( output, e, deiconified );
                }

                public void windowIconified(WindowEvent e) {
                    invokeScript( output, e, iconified );
                }

                public void windowOpened(WindowEvent e) {                     
                    invokeScript( output, e, opened );
                }
            };
            tag.addWindowListener(listener);
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    


    /**
     * Sets the name of the variable to use to expose the Event object
     */
    public void setVar(String var) {
        this.var = var;
    }
            
    /**
     * Sets the Script to be executed when the window is activated.
     */
    public void setActivated(Script activated) {
        this.activated = activated;
    }

    /**
     * Sets the Script to be executed when the window is closed.
     */
    public void setClosed(Script closed) {
        this.closed = closed;
    }

    /**
     * Sets the Script to be executed when the window is closing.
     */
    public void setClosing(Script closing) {
        this.closing = closing;
    }

    /**
     * Sets the Script to be executed when the window is deactivated.
     */
    public void setDeactivated(Script deactivated) {
        this.deactivated = deactivated;
    }

    /**
     * Sets the Script to be executed when the window is deiconified.
     */
    public void setDeiconified(Script deiconified) {
        this.deiconified = deiconified;
    }

    /**
     * Sets the Script to be executed when the window is iconified.
     */
    public void setIconified(Script iconified) {
        this.iconified = iconified;
    }

    /**
     * Sets the Script to be executed when the window is opened.
     */
    public void setOpened(Script opened) {
        this.opened = opened;
    }


    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected void invokeScript(XMLOutput output, WindowEvent event, Script script) {
        if ( var != null ) {
            // define a variable of the event
            context.setVariable(var, event);
        }
        
        try {
            if ( script != null ) {
                script.run(context, output );
            }
            else {
                // invoke the body
                invokeBody(output);
            }
        }
        catch (Exception e) {
            log.error( "Caught exception processing window event: " + event, e );
        }
    }
        
}
