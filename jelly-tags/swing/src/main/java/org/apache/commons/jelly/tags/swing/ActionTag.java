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

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a Swing Action which on invocation will execute the body of this tag.
 * The Action is then output as a variable for reuse if the 'var' attribute is specified
 * otherwise the action is added to the parent JellySwing widget.
 */
public class ActionTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ActionTag.class);

    public ActionTag() {
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the Action object for this tag
     */
    public Action getAction() {
        return (Action) getBean();
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * An existing Action could be specified via the 'action' attribute or an action class
     * may be specified via the 'class' attribute, otherwise a default Action class is created.
     */
    @Override
    protected Class convertToClass(final Object classObject) throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        return super.convertToClass(classObject);
    }

    /**
     * An existing Action could be specified via the 'action' attribute or an action class
     * may be specified via the 'class' attribute, otherwise a default Action class is created.
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output) throws JellyTagException {
        Action action = (Action) attributes.remove( "action" );
        if ( action == null ) {
            if (theClass != null ) {

                try {
                    return theClass.getConstructor().newInstance();
                } catch (final ReflectiveOperationException e) {
                    throw new JellyTagException(e);
                }

            }
            action = new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    context.setVariable( "event", event );
                    try {
                        ActionTag.super.invokeBody(output);
                    }
                    catch (final Exception e) {
                        log.error( "Caught: " + e, e );
                    }
                }
            };
        }
        return action;
    }

	@Override
    public void invokeBody(final XMLOutput output) {
		// do nothing
	}

    /**
     * Either defines a variable or adds the current component to the parent
     */
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        else {
            final ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
            if ( tag == null ) {
                throw new JellyTagException( "Either the 'var' attribute must be specified to export this Action or this tag must be nested within a JellySwing widget tag" );
            }
            tag.setAction((Action) bean);
        }
    }

    /**
     * Perform the strange setting of Action properties using its custom API
     */
    @Override
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {
        final Action action = getAction();

        final String enabled = "enabled";
        if (attributes.containsKey(enabled)) {
            try {
                BeanUtils.copyProperty(action, enabled, attributes.get(enabled));
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException("Failed to set the enabled property.", e);
            }

            attributes.remove(enabled);
        }

        for ( final Iterator iter = attributes.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();

            // typically standard Action names start with upper case, so lets upper case it
            name = capitalize(name);
            final Object value = entry.getValue();

            action.putValue( name, value );
        }
    }

    protected String capitalize(final String text) {
        final char ch = text.charAt(0);
        if ( Character.isUpperCase( ch ) ) {
            return text;
        }
        final StringBuilder buffer = new StringBuilder(text.length());
        buffer.append( Character.toUpperCase( ch ) );
        buffer.append( text.substring(1) );
        return buffer.toString();
    }

}
