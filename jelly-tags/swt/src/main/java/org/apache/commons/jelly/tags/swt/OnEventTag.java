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
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException("var");
        }
        if (type == null) {
            throw new MissingAttributeException("type");
        }

        final Widget widget = getParentWidget();
        if (widget == null) {
            throw new JellyTagException("This tag must be nested within a widget tag");
        }

        final int eventType = getEventType(type);
        if (eventType == 0) {
            throw new JellyTagException("No event type specified, could not understand: " + type);
        }

        this.output = output;
        widget.addListener(eventType, this);
    }

    /**
     * Parses the given event type String and returns the SWT event type code
     *
     * @param type is the String event type
     * @return the SWT integer event type
     */
    protected int getEventType(final String type) throws JellyTagException {
        return SwtHelper.parseStyle(SWT.class, type, false);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the parent widget which this widget will be added to.
     */
    public Widget getParentWidget() {
        final WidgetTag tag = (WidgetTag) findAncestorWithClass(WidgetTag.class);
        if (tag != null) {
            return tag.getWidget();
        }
        return null;
    }

    /**
     * Returns the type.
     * @return String
     */
    public String getType() {
        return type;
    }

    // Listener interface
    //-------------------------------------------------------------------------
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent(final Event event) {
        try {
            context.setVariable(var, event);
            invokeBody(output);
        }
        catch (final Exception e) {
            log.error("Caught exception: " + e + " while processing event: " + event, e);
        }
    }

    /**
     * Sets the type of the event listener to listen for.
     *
     * @param type The type of the event to listen for
     */
    public void setType(final String type) {
        this.type = type;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the variable to use to expose the event object when
     * it is fired. If not specified this defaults to "event"
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
