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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.Border;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.swing.converters.DebugGraphicsConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This tag creates a Swing component and adds it to its parent tag, optionally declaring this
 * component as a variable if the <em>var</em> attribute is specified.
 *
 * <p>
 * This tag clears the reference to it's bean after doTag runs.
 * This means that child tags can access the component (bean) normally
 * during execution but should not hold a reference to this
 * tag after their doTag completes.
 * </p>
 */
public class ComponentTag extends UseBeanTag implements ContainerTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ComponentTag.class);

    /** This is a converter that might normally be used through the
     * BeanUtils product. However, it only applies to one Component
     * property and not to all ints, so it's not registered with BeanUtils.
     */
    private static final DebugGraphicsConverter debugGraphicsConverter = new DebugGraphicsConverter();

    /** The factory of widgets */
    private Factory factory;

    private String tagName = null;

    private XMLOutput currentOutput = null;

    public ComponentTag() {
    }

    public ComponentTag(final Factory factory) {
        this.factory = factory;
    }

    /**
     * Adds a child component to this parent
     */
    @Override
    public void addChild(final Component component, final Object constraints) throws JellyTagException {
        final Object parent = getBean();
        if ( parent instanceof JFrame && component instanceof JMenuBar ) {
            final JFrame frame = (JFrame) parent;
            frame.setJMenuBar( (JMenuBar) component );
        }
        else if ( parent instanceof RootPaneContainer ) {
            final RootPaneContainer rpc = (RootPaneContainer) parent;
            if (constraints != null) {
                rpc.getContentPane().add( component, constraints );
            }
            else {
                rpc.getContentPane().add( component);
            }
        }
        else if ( parent instanceof JScrollPane ) {
            final JScrollPane scrollPane = (JScrollPane) parent;
            scrollPane.setViewportView( component );
        }
        else if ( parent instanceof JSplitPane) {
            final JSplitPane splitPane = (JSplitPane) parent;
            if ( splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ) {
                if ( splitPane.getTopComponent() == null ) {
                    splitPane.setTopComponent( component );
                }
                else {
                    splitPane.setBottomComponent( component );
                }
            } else if ( splitPane.getLeftComponent() == null ) {
                splitPane.setLeftComponent( component );
            }
            else {
                splitPane.setRightComponent( component );
            }
        }
        else if ( parent instanceof JMenuBar && component instanceof JMenu ) {
            final JMenuBar menuBar = (JMenuBar) parent;
            menuBar.add( (JMenu) component );
        }
        else if ( parent instanceof Container ) {
            final Container container = (Container) parent;
            if (constraints != null) {
                container.add( component, constraints );
            }
            else {
                container.add( component );
            }
        }
    }

    /**
     * Adds a FocusListener to this component
     */
    public void addFocusListener(final FocusListener listener) throws JellyTagException {
        final Component component = getComponent();
        component.addFocusListener(listener);
    }

    /**
     * Adds a KeyListener to this component
     */
    public void addKeyListener(final KeyListener listener) throws JellyTagException {
        final Component component = getComponent();
        component.addKeyListener(listener);
    }


	/**
     * Adds a WindowListener to this component
     */
    public void addWindowListener(final WindowListener listener) throws JellyTagException {
        final Component component = getComponent();
        if ( component instanceof Window ) {
            final Window window = (Window) component;
            window.addWindowListener(listener);
        }
    }

	/** Sets the bean to null, to prevent it from
     * sticking around in the event that this tag instance is
     * cached. This method is called at the end of doTag.
     *
     */
    protected void clearBean() {
        setBean(null);
    }

	/**
     * A class may be specified otherwise the Factory will be used.
     */
    @Override
    protected Class convertToClass(final Object classObject) throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        return super.convertToClass(classObject);
    }

    /**Overrides the default UseBean functionality to clear the bean after the
     * tag runs. This prevents us from keeping references to heavy Swing objects
     * around for longer than they are needed.
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        super.doTag(output);
        clearBean();
    }

    /**
     * @return the visible component, if there is one.
     */
    public Component getComponent() {
        final Object bean = getBean();
        if ( bean instanceof Component ) {
            return (Component) bean;
        }
        return null;
    }

    protected Object getConstraint() {
        return null;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * A class may be specified otherwise the Factory will be used.
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output) throws JellyTagException {
		if (attributes.containsKey("tagName")) {
			this.setTagName((String)attributes.get("tagName"));
			addIgnoreProperty("tagName");
		}
	 if (tagName!=null) {
			context.setVariable(tagName,this);
			currentOutput = output;
		}
        try {
            if (theClass != null ) {
                return theClass.getConstructor().newInstance();
            }
            return factory.newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new JellyTagException(e);
        }
    }

    // ContainerTag interface
    //-------------------------------------------------------------------------

    /**
     * Either defines a variable or adds the current component to the parent
     */
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        final Component component = getComponent();
        if ( component != null ) {
            final ContainerTag parentTag = (ContainerTag) findAncestorWithClass( ContainerTag.class );
            if ( parentTag != null ) {
                parentTag.addChild(component, getConstraint());
            } else if (var == null) {
                throw new JellyTagException( "The 'var' attribute must be specified or this tag must be nested inside a JellySwing container tag like a widget or a layout" );
            }
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the Action of this component
     */
    public void setAction(final Action action) throws JellyTagException {
        final Component component = getComponent();
        if ( component != null ) {
            // lets just try set the 'action' property
            try {
                BeanUtils.setProperty( component, "action", action );
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Handles weird properties that don't quite match the Java Beans contract
     */
    @Override
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {

            final Component component = getComponent();
            if (component != null) {
                if (attributes.containsKey("location")) {
                    final Object value = attributes.get("location");
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
                    addIgnoreProperty("location");
                }

                if (attributes.containsKey("size")) {
                    final Object value = attributes.get("size");
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
                    addIgnoreProperty("size");
                }


                if (attributes.containsKey("debugGraphicsOptions")) {
                    try {
                        final Object o = debugGraphicsConverter.convert(attributes.get("debugGraphicsOptions"));
                        attributes.put("debugGraphicsOptions", o);
                    } catch (final IllegalArgumentException e) {
                        throw new JellyTagException(e);
                    }
                }

                if (attributes.containsKey("debugGraphics")) {
                    try {
                        final Object o = debugGraphicsConverter.convert(attributes.get("debugGraphics"));
                        attributes.put("debugGraphicsOptions", o);
                    } catch (final IllegalArgumentException e) {
                        throw new JellyTagException(e);
                    }

                    addIgnoreProperty("debugGraphics");
                }

             super.setBeanProperties(bean, attributes);
        }
    }

    /**
     * Sets the Border of this component
     */
    public void setBorder(final Border border) throws JellyTagException {
        final Component component = getComponent();
        if ( component != null ) {
            try {
                // lets just try set the 'border' property
                BeanUtils.setProperty( component, "border", border );
            }
            catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Sets the Font of this component
     */
    public void setFont(final Font font) throws JellyTagException {
        final Component component = getComponent();
        if ( component != null ) {
            // lets just try set the 'font' property
            try {
                BeanUtils.setProperty( component, "font", font );
            }
            catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /**
     * Sets the LayoutManager of this component
     */
    public void setLayout(final LayoutManager layout) throws JellyTagException {
        Component component = getComponent();
        if ( component != null ) {
            if ( component instanceof RootPaneContainer ) {
                final RootPaneContainer rpc = (RootPaneContainer) component;
                component = rpc.getContentPane();
            }

            try {
                // lets just try set the 'layout' property
                BeanUtils.setProperty( component, "layout", layout );
            }
            catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
    }

    /** Puts this tag into the context under the given name
	 * allowing later calls to rerun().
	 * For example, it makes sense to use ${myTag.rerun()} as a child
	 * of an {@code action} element.
	 *
	 * @param name name to be used
	 */
	public void setTagName(final String name) {
		this.tagName = name;
	}

    @Override
    public String toString() {
		final Component comp = getComponent();
        String componentName = comp!=null ? comp.getName() : null;
        if (comp!=null && (componentName == null || componentName.length() == 0)) {
            componentName = getComponent().toString();
        }
        return "ComponentTag with bean " + componentName;
    }
}
