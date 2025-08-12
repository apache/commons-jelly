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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

/**
 * Creates a new Layout implementations and adds it to the parent Widget.
 * @version 1.1
 */
public class LayoutTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTag.class);

    public LayoutTag(final Class layoutClass) {
        super(layoutClass);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.jelly.tags.swt.LayoutTagSupport#convertValue(Object, String, Object)
     */
    @Override
    protected Object convertValue(final Object bean, final String name, final Object value)
        throws JellyTagException {

        if (bean instanceof FillLayout
            && name.equals("type")
            && value instanceof String) {
            final int style = SwtHelper.parseStyle(SWT.class, (String) value);
            return new Integer(style);
        }
        return super.convertValue(bean, name, value);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * @return the Layout if there is one otherwise null
     */
    public Layout getLayout() {
        final Object bean = getBean();
        if (bean instanceof Layout) {
            return (Layout) bean;
        }
        return null;
    }

    /**
     * Either defines a variable or adds the current component to the parent
     */
    @Override
    protected void processBean(final String var, final Object bean)
        throws JellyTagException {
        super.processBean(var, bean);

        final Widget parent = getParentWidget();

        if (!(parent instanceof Composite)) {
            throw new JellyTagException("This tag must be nested within a composite widget tag");
        }
        final Composite composite = (Composite) parent;
        composite.setLayout(getLayout());
    }

}
