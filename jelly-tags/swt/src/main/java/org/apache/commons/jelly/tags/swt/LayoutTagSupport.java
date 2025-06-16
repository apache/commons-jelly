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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Widget;

/**
 * An abstract base class for Layout or LayoutData tags.
 * @version 1.1
 */
public abstract class LayoutTagSupport extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTagSupport.class);

    private String var;

    public LayoutTagSupport(final Class layoutClass) {
        super(layoutClass);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Provides a strategy method that allows values to be converted,
     * particularly to support integer enumerations and String representations.
     *
     * @param bean is the bean on which the property is to be set
     * @param name is the name of the property
     * @param value the value of the property
     * @return the new value
     */
    protected Object convertValue(final Object bean, final String name, final Object value)
        throws JellyTagException {
        return value;
    }

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

    // Implementation methods
    //-------------------------------------------------------------------------
    /**
     * Either defines a variable or adds the current component to the parent
     */
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
    }

    /**
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#setBeanProperties(java.lang.Object, java.util.Map)
     */
    @Override
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {

        if (bean != null) {
            final Class theClass = bean.getClass();
            for (final Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                final Map.Entry entry = (Map.Entry) iter.next();
                final String name = (String) entry.getKey();
                Object value = entry.getValue();

                value = convertValue(bean, name, value);

                try {
                    // lets first see if there's a field available
                    final Field field = theClass.getField(name);
                    if (field != null) {
                        if (value instanceof String) {
                            value = ConvertUtils.convert((String) value, field.getType());
                        }
                        field.set(bean, value);
                    } else {
                        BeanUtils.setProperty(bean, name, value);
                    }
                } catch (final NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                    throw new JellyTagException(e);
                }
            }
        }
    }

    /**
     * Sets the name of the variable to use to expose the new Layout object.
     * If this attribute is not set then the parent widget tag will have its
     * layout property set.
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
