/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/WidgetTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
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
 * WidgetTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;

/** 
 * This tag creates an SWT widget based on the parent tag, optionally declaring
 * this widget as a variable if the <i>var</i> attribute is specified.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class WidgetTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(WidgetTag.class);
    
    
    public WidgetTag(Class widgetClass) {
        super(widgetClass);
    }

    public String toString() {
        return "WidgetTag[widget=" + getWidget() + "]";
    }


    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the visible widget, if there is one.
     */
    public Widget getWidget() {
        Object bean = getBean();
        if ( bean instanceof Widget ) {
            return (Widget) bean;
        }
        return null;
    }

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

    
    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * Factory method to create a new widget
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws Exception {
        int style = getStyle(attributes);
        
        // now lets call the constructor with the parent
        Widget parent = getParentWidget();
        Widget widget = (Widget) createWidget(theClass, parent, style);
        if (parent != null) {
            attachWidgets(parent, widget);
        }
        return widget; 
    }
    
    /**
     * Provides a strategy method to allow a new child widget to be attached to
     * its parent
     * 
     * @param parent is the parent widget which is never null
     * @param widget is the new child widget to be attached to the parent
     */
    protected void attachWidgets(Widget parent, Widget widget) {
    }
    
    /**
     * Factory method to create an instance of the given Widget class with
     * the given parent and SWT style
     * 
     * @param theClass is the type of widget to create
     * @param parent is the parent widget
     * @param style the SWT style code
     * @return the new Widget
     */
    protected Object createWidget(Class theClass, Widget parent, int style) throws Exception {
        if (theClass == null) {
            throw new JellyException( "No Class available to create the new widget");
        }
        if (parent == null) {
            // lets try call a constructor with a single style
            Class[] types = { int.class };
            Constructor constructor = theClass.getConstructor(types);
            if (constructor != null) {
                Object[] arguments = { new Integer(style)};
                return constructor.newInstance(arguments);
            }
        }
        else {
            // lets try to find the constructor with 2 arguments with the 2nd argument being an int
            Constructor[] constructors = theClass.getConstructors();
            if (constructors != null) {
                for (int i = 0, size = constructors.length; i < size; i++ ) {
                    Constructor constructor = constructors[i];
                    Class[] types = constructor.getParameterTypes();
                    if (types.length == 2 && types[1].isAssignableFrom(int.class)) {
                        if (types[0].isAssignableFrom(parent.getClass())) {
                            Object[] arguments = { parent, new Integer(style)};
                            return constructor.newInstance(arguments);
                        }
                    }
                }
            }
        }
        return theClass.newInstance();
    }
    
    /**
     * Creates the SWT style code for the current attributes
     * @return the SWT style code
     */
    protected int getStyle(Map attributes) throws Exception {
        String text = (String) attributes.remove("style");
        if (text != null) {
            return SwtHelper.parseStyle(SWT.class, text);
        }
        return SWT.DEFAULT;
    }
}
