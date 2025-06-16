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
package org.apache.commons.jelly.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaClass;
import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * This tag is bound onto a {@link DynaClass} instance.
 * When the tag is invoked a {@link DynaBean} will be created using the tags attributes.
 * So this class is like a {@link DynaBean} implementation of {@link DynamicBeanTag}
 */
public class DynamicDynaBeanTag extends DynaBeanTagSupport implements BeanSource {

    /** The bean class */
    private final DynaClass beanClass;

    /**
     * the tag attribute name that is used to declare the name
     * of the variable to export after running this tag
     */
    private final String variableNameAttribute;

    /** The current variable name that the bean should be exported as */
    private String var;

    /** The set of attribute names we've already set */
    private final Set setAttributesSet = new HashSet();

    /** The attribute definitions */
    private final Map attributes;

    public DynamicDynaBeanTag(final DynaClass beanClass, final Map attributes, final String variableNameAttribute) {
        this.beanClass = beanClass;
        this.attributes = attributes;
        this.variableNameAttribute = variableNameAttribute;
    }

    @Override
    public void beforeSetAttributes() throws JellyTagException {
        // create a new dynabean before the attributes are set
        try {
            setDynaBean(beanClass.newInstance());
        } catch (final IllegalAccessException | InstantiationException e) {
            throw new JellyTagException("Could not instantiate dynabean", e);
        }

        setAttributesSet.clear();
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        // lets find any attributes that are not set and
        for ( final Iterator iter = attributes.values().iterator(); iter.hasNext(); ) {
            final Attribute attribute = (Attribute) iter.next();
            final String name = attribute.getName();
            if ( ! setAttributesSet.contains( name ) ) {
                if ( attribute.isRequired() ) {
                    throw new MissingAttributeException(name);
                }
                // lets get the default value
                Object value = null;
                final Expression expression = attribute.getDefaultValue();
                if ( expression != null ) {
                    value = expression.evaluate(context);
                }

                // only set non-null values?
                if ( value != null ) {
                    super.setAttribute(name, value);
                }
            }
        }

        invokeBody(output);

        // export the bean if required
        if ( var != null ) {
            context.setVariable(var, getDynaBean());
        }
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * @return the bean that has just been created
     */
    @Override
    public Object getBean() {
        return getDynaBean();
    }

    @Override
    public void setAttribute(final String name, final Object value) throws JellyTagException {
        boolean isVariableName = false;
        if ( variableNameAttribute != null && variableNameAttribute.equals( name ) ) {
            if (value == null) {
                var = null;
            }
            else {
                var = value.toString();
            }
            isVariableName = true;
        }
        if (! isVariableName) {

            // #### strictly speaking we could
            // know what attributes are specified at compile time
            // so this dynamic set is unnecessary
            setAttributesSet.add(name);

            // we could maybe implement attribute specific validation here

            super.setAttribute(name, value);
        }
    }
}
