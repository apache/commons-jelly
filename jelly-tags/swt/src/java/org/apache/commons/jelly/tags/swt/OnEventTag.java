/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/LayoutTagSupport.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
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
 * 
 * LayoutTagSupport.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/** 
 * A tag which implements a Listener to allow events to be processed by
 * Jelly scripts
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class OnEventTag extends TagSupport implements Listener {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(OnEventTag.class);

    private String var = "event";
    private String type; 
    private XMLOutput output;

    public OnEventTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    
    /**
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException("var");
        }
        if (type == null) {
            throw new MissingAttributeException("type");
        }
        
        Widget widget = getParentWidget();
        if (widget == null) {
            throw new JellyTagException("This tag must be nested within a widget tag");
        }

        
        int eventType = getEventType(type);
        if (eventType == 0) {
            throw new JellyTagException("No event type specified, could not understand: " + type);
        }
        
        this.output = output;
        widget.addListener(eventType, this);
    }
    
    // Listener interface
    //-------------------------------------------------------------------------
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
        try {
            context.setVariable(var, event);
            invokeBody(output);
        }
        catch (Exception e) {
            log.error("Caught exception: " + e + " while processing event: " + event, e);
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the parent widget which this widget will be added to.
     */
    public Widget getParentWidget() {
        WidgetTag tag = (WidgetTag) findAncestorWithClass(WidgetTag.class);
        if (tag != null) {
            return tag.getWidget();
        }
        return null;
    }

    /**
     * Sets the name of the variable to use to expose the event object when
     * it is fired. If not specified this defaults to "event"
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Returns the type.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the event listener to listen for.
     * 
     * @param type The type of the event to listen for
     */
    public void setType(String type) {
        this.type = type;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Parses the given event type String and returns the SWT event type code
     *
     * @param type is the String event type
     * @return the SWT integer event type
     */
    protected int getEventType(String type) throws JellyTagException {
        return SwtHelper.parseStyle(SWT.class, type, false);
    }
    
}
