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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag creates a Swing component and adds it to its parent tag, optionally declaring this
 * component as a variable if the <i>var</i> attribute is specified.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ComponentTag extends UseBeanTag implements ContainerTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ComponentTag.class);

    /** the factory of widgets */
    private Factory factory;
    
    public ComponentTag() {
    }

    public ComponentTag(Factory factory) {
        this.factory = factory;
    }

    public String toString() {
        String componentName = getComponent().getName();
        if (componentName == null || componentName.length() == 0)
            componentName = getComponent().toString();
        return "ComponentTag with bean " + componentName;
    }

    /**
     * Sets the Action of this component
     */
    public void setAction(Action action) throws JellyTagException {
        Component component = getComponent();
        if ( component != null ) {
            // lets just try set the 'action' property
            try {
                BeanUtils.setProperty( component, "action", action );
            } catch (IllegalAccessException e) {
                throw new JellyTagException(e);
            } catch (InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Sets the Font of this component
     */
    public void setFont(Font font) throws JellyTagException {
        Component component = getComponent();
        if ( component != null ) {
            // lets just try set the 'font' property
            try {
                BeanUtils.setProperty( component, "font", font );
            } 
            catch (IllegalAccessException e) {
                throw new JellyTagException(e);
            }
            catch (InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Sets the Border of this component
     */
    public void setBorder(Border border) throws JellyTagException {
        Component component = getComponent();
        if ( component != null ) {
            try {
                // lets just try set the 'border' property
                BeanUtils.setProperty( component, "border", border );
            }
            catch (IllegalAccessException e) {
                throw new JellyTagException(e);
            }
            catch (InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Sets the LayoutManager of this component
     */
    public void setLayout(LayoutManager layout) throws JellyTagException {
        Component component = getComponent();
        if ( component != null ) {
            if ( component instanceof RootPaneContainer ) {
                RootPaneContainer rpc = (RootPaneContainer) component;
                component = rpc.getContentPane();
            }
            
            try {
                // lets just try set the 'layout' property
                BeanUtils.setProperty( component, "layout", layout );
            }
            catch (IllegalAccessException e) {
                throw new JellyTagException(e);
            }
            catch (InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Adds a WindowListener to this component
     */
    public void addWindowListener(WindowListener listener) {
        Component component = getComponent();
        if ( component instanceof Window ) {
            Window window = (Window) component;
            window.addWindowListener(listener);
        }
    }
    
    /**
     * Adds a FocusListener to this component
     */
    public void addFocusListener(FocusListener listener) {
        Component component = getComponent();
        component.addFocusListener(listener);    
    }
    
    /**
     * Adds a KeyListener to this component
     */
    public void addKeyListener(KeyListener listener) {
        Component component = getComponent();
        component.addKeyListener(listener);
    }

    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the visible component, if there is one.
     */
    public Component getComponent() {
        Object bean = getBean();
        if ( bean instanceof Component ) {
            return (Component) bean;
        }
        return null;
    }    
    
    
    // ContainerTag interface
    //-------------------------------------------------------------------------                    
    
    /**
     * Adds a child component to this parent
     */
    public void addChild(Component component, Object constraints) {
        Object parent = getBean();
        if ( parent instanceof JFrame && component instanceof JMenuBar ) {
            JFrame frame = (JFrame) parent;
            frame.setJMenuBar( (JMenuBar) component );
        }
        else if ( parent instanceof RootPaneContainer ) {
            RootPaneContainer rpc = (RootPaneContainer) parent;
            rpc.getContentPane().add( component );
        }
        else if ( parent instanceof JScrollPane ) {
            JScrollPane scrollPane = (JScrollPane) parent;
            scrollPane.setViewportView( component );
        }
        else if ( parent instanceof JSplitPane) {
            JSplitPane splitPane = (JSplitPane) parent;
            if ( splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ) {
                if ( splitPane.getTopComponent() == null ) {
                    splitPane.setTopComponent( component );
                }
                else {
                    splitPane.setBottomComponent( component );
                }
            }
            else {
                if ( splitPane.getLeftComponent() == null ) {
                    splitPane.setLeftComponent( component );
                }
                else {
                    splitPane.setRightComponent( component );
                }
            }
        }
        else if ( parent instanceof JMenuBar && component instanceof JMenu ) {
            JMenuBar menuBar = (JMenuBar) parent;
            menuBar.add( (JMenu) component );
        }
        else if ( parent instanceof Container ) {
            Container container = (Container) parent;
            if (constraints != null) {
                container.add( component, constraints );
            }
            else {
                container.add( component );
            }
        }            
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * A class may be specified otherwise the Factory will be used.
     */
    protected Class convertToClass(Object classObject) throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        else {
            return super.convertToClass(classObject);
        }
    }
    
    /**
     * A class may be specified otherwise the Factory will be used.
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws JellyTagException {
        try {
            if (theClass != null ) {
                return theClass.newInstance();
            } else {
                return factory.newInstance();
            }
        } catch (IllegalAccessException e) {
            throw new JellyTagException(e);
        } catch (InstantiationException e) {
            throw new JellyTagException(e);
        }
    }
    

    /**
     * Either defines a variable or adds the current component to the parent
     */    
    protected void processBean(String var, Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        Component component = getComponent();
        if ( component != null ) {
            ContainerTag parentTag = (ContainerTag) findAncestorWithClass( ContainerTag.class );
            if ( parentTag != null ) {
                parentTag.addChild(component, getConstraint());
            }
            else {
                if (var == null) {
                    throw new JellyTagException( "The 'var' attribute must be specified or this tag must be nested inside a JellySwing container tag like a widget or a layout" );
                }
            }
        }
    }
    
    /**
     * Patch to handle wierd properties that don't quite match the Java Beans contract
     */
    protected void setBeanProperties(Object bean, Map attributes) throws JellyTagException {
        for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            Object value = entry.getValue();

            // ### special hacks for properties that don't introspect properly            
            Component component = getComponent();
            if (component != null) {
                if (name.equals("location")) {
                    Point p = null;
                    if (value instanceof Point) {
                        p = (Point) value;
                    }
                    else if (value != null) {
                        p =
                            (Point) ConvertUtils.convert(
                                value.toString(),
                                Point.class);
                    }
                    component.setLocation(p);
                }
                else if (name.equals("size")) {
                    Dimension d = null;
                    if (value instanceof Dimension) {
                        d = (Dimension) value;
                    }
                    else if (value != null) {
                        d =
                            (Dimension) ConvertUtils.convert(
                                value.toString(),
                                Dimension.class);
                    }
                    component.setSize(d);
                }
                else if (
                    name.equalsIgnoreCase("background")
                        || name.equalsIgnoreCase("foreground")) {
                    Color c = null;
                    if (value instanceof Color) {
                        c = (Color) value;
                    }
                    else if (value != null) {
                        c =
                            (Color) ConvertUtils.convert(
                                value.toString(),
                                Color.class);
                    }

                    if (name.equalsIgnoreCase("background")) {
                        component.setBackground(c);
                    }
                    else {
                        component.setForeground(c);
                    }
                }
                else if (
                    name.equalsIgnoreCase("debugGraphicsOption")
                        || name.equalsIgnoreCase("debugGraphics")
                        || name.equalsIgnoreCase("debug")) {

                    Integer v = null;
                    if (!(value instanceof Integer))
                        v =
                            (Integer) ConvertUtils.convert(
                                value.toString(),
                                Integer.class);
                    else
                        v = (Integer) value;
                    if (!(component instanceof JComponent))
                        throw new IllegalArgumentException("DebugGraphics can only be set on a JComponent subclass.");
                    ((JComponent) component).setDebugGraphicsOptions(
                        v.intValue());
                }
                else {
                    try {
                        BeanUtils.setProperty(component, name, value);
                    } catch (IllegalAccessException e) {
                        throw new JellyTagException(e);
                    } catch (InvocationTargetException e) {
                        throw new JellyTagException(e);
                    }
                }
            }
            else {
                try {
                    BeanUtils.setProperty(bean, name, value);
                } catch (IllegalAccessException e) {
                    throw new JellyTagException(e);
                } catch (InvocationTargetException e) {
                    throw new JellyTagException(e);
                }
            }
        }
    }

    protected Object getConstraint() {
        return null;
    }
}
