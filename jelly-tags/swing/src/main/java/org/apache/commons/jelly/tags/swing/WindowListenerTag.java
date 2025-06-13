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
package org.apache.commons.jelly.tags.swing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a WindowListener which is attached to its parent window control which will invoke
 * named Jelly scripts as window events are fired, or will invoke its body if there is no script
 * specified for the named event type.
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
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        // now lets add this action to its parent if we have one
        final ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag != null ) {
            final WindowListener listener = new WindowListener() {
                @Override
                public void windowActivated(final WindowEvent e) {
                    invokeScript( output, e, activated );
                }

                @Override
                public void windowClosed(final WindowEvent e) {
                    invokeScript( output, e, closed );
                }

                @Override
                public void windowClosing(final WindowEvent e) {
                    invokeScript( output, e, closing );
                }

                @Override
                public void windowDeactivated(final WindowEvent e) {
                    invokeScript( output, e, deactivated );
                }

                @Override
                public void windowDeiconified(final WindowEvent e) {
                    invokeScript( output, e, deiconified );
                }

                @Override
                public void windowIconified(final WindowEvent e) {
                    invokeScript( output, e, iconified );
                }

                @Override
                public void windowOpened(final WindowEvent e) {
                    invokeScript( output, e, opened );
                }
            };
            tag.addWindowListener(listener);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    // Implementation methods
    //-------------------------------------------------------------------------
    protected void invokeScript(final XMLOutput output, final WindowEvent event, final Script script) {
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
        catch (final Exception e) {
            log.error( "Caught exception processing window event: " + event, e );
        }
    }

    /**
     * Sets the Script to be executed when the window is activated.
     */
    public void setActivated(final Script activated) {
        this.activated = activated;
    }

    /**
     * Sets the Script to be executed when the window is closed.
     */
    public void setClosed(final Script closed) {
        this.closed = closed;
    }

    /**
     * Sets the Script to be executed when the window is closing.
     */
    public void setClosing(final Script closing) {
        this.closing = closing;
    }

    /**
     * Sets the Script to be executed when the window is deactivated.
     */
    public void setDeactivated(final Script deactivated) {
        this.deactivated = deactivated;
    }

    /**
     * Sets the Script to be executed when the window is deiconified.
     */
    public void setDeiconified(final Script deiconified) {
        this.deiconified = deiconified;
    }

    /**
     * Sets the Script to be executed when the window is iconified.
     */
    public void setIconified(final Script iconified) {
        this.iconified = iconified;
    }

    /**
     * Sets the Script to be executed when the window is opened.
     */
    public void setOpened(final Script opened) {
        this.opened = opened;
    }

    /**
     * Sets the name of the variable to use to expose the Event object
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
