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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Creates a LayoutData object and sets it on the parent Widget.
 * @version 1.1
 */
public class LayoutDataTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutDataTag.class);

    public LayoutDataTag(final Class layoutDataClass) {
        super(layoutDataClass);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Either defines a variable or adds the current component to the parent
     */
    @Override
    protected void processBean(final String var, final Object bean)
        throws JellyTagException {
        super.processBean(var, bean);

        final Widget parent = getParentWidget();

        if (!(parent instanceof Control)) {
            throw new JellyTagException("This tag must be nested within a control widget tag");
        }
        final Control control = (Control) parent;
        control.setLayoutData(getBean());
    }

    /**
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    @Override
    protected Object newInstance(
        final Class theClass,
        final Map attributes,
        final XMLOutput output)
        throws JellyTagException {

        final String text = (String) attributes.remove("style");
        if (text != null) {
            final int style = SwtHelper.parseStyle(theClass, text);

            // now lets try invoke a constructor
            final Class[] types = { int.class };

            try {
                final Constructor constructor = theClass.getConstructor(types);
                if (constructor != null) {
                    final Object[] values = { new Integer(style)};
                    return constructor.newInstance(values);
                }
            } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
        return super.newInstance(theClass, attributes, output);
    }

    /**
     * @see org.apache.commons.jelly.tags.swt.LayoutTagSupport#convertValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    protected Object convertValue(final Object bean, final String name, final Object value)
        throws JellyTagException {

        if ((bean instanceof GridData) && (name.endsWith("Alignment") && value instanceof String)) {
            final int style =
                SwtHelper.parseStyle(bean.getClass(), (String) value);
            return new Integer(style);
        }
        return super.convertValue(bean, name, value);
    }

}
