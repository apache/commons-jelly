/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowListener;

import javax.swing.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.collections.BeanMap;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag creates a Swing component and adds it to its parent tag, optionally declaring this
 * component as a variable if the <i>var</i> attribute is specified.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ComponentTag extends DynaBeanTagSupport implements BeanSource {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ComponentTag.class);

    /** the factory of widgets */
    private Factory factory;
    
    /** the current bean instance */
    private Object bean;

    /** the current variable name that the bean should be exported as */
    private String var;

    public ComponentTag(Factory factory) {
        this.factory = factory;
    }

    /**
     * Adds a child component to this parent
     */
    public void addChild(Component component) {
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
            container.add( component );
        }            
    }


    /**
     * Sets the Action of this component
     */
    public void setAction(Action action) throws Exception {
        Component component = getComponent();
        if ( component != null ) {
            // lets just try set the 'action' property
            BeanUtils.setProperty( component, "action", action );
        }
    }

    /**
     * Adds a WindowListener to this component
     */
    public void addWindowListener(WindowListener listener) throws Exception {
        Component component = getComponent();
        if ( component instanceof Window ) {
            Window window = (Window) component;
            window.addWindowListener(listener);
        }
    }

    // DynaTag interface
    //-------------------------------------------------------------------------                    
    public void beforeSetAttributes() throws Exception {
        // create a new dynabean before the attributes are set
        bean = factory.newInstance();
        setDynaBean( new ConvertingWrapDynaBean( bean ) );
    }

    public void setAttribute(String name, Object value) throws Exception {        
        if (name.equals( "var" ) ) {
            this.var = value.toString();
        }
        else {
            // ### special hacks for properties that don't introspect properly            
            Component component = getComponent();
            if ( component != null ) {
                if ( name.equals( "location" ) ) {
                    Point p = null;
                    if ( value instanceof Point ) {
                        p = (Point) value;
                    }
                    else if ( value != null) {
                        p = (Point) ConvertUtils.convert( value.toString(), Point.class );
                    }
                    component.setLocation(p);
                }
                else if ( name.equals( "size" ) ) {                
                    Dimension d = null;
                    if ( value instanceof Dimension ) {
                        d = (Dimension) value;
                    }
                    else if ( value != null) {
                        d = (Dimension) ConvertUtils.convert( value.toString(), Dimension.class );
                    }
                    component.setSize(d);
                }                    
                else {
                    super.setAttribute(name, value);
                }
            }
            else {
                super.setAttribute(name, value);
            }
        }
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {

        // export the bean if required
        if ( var != null ) {
            context.setVariable(var, bean);
        }
        
        invokeBody(output);

        // now we should add this component to its parent...
        // This is currently quite simplistic. 
        // We could well support layout managers and such like

        Component component = getComponent();
        if ( component != null ) {
            ComponentTag parentTag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
            if ( parentTag != null ) {
                parentTag.addChild(component);
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the bean that has just been created 
     */
    public Object getBean() {
        return bean;
    }

    /**
     * @return the visible component, if there is one.
     */
    public Component getComponent() {
        if ( bean instanceof Component ) {
            return (Component) bean;
        }
        return null;
    }    
}
