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
package org.apache.commons.jelly.tags.jface;

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.swt.WidgetTag;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * This tag creates an JFace Viewer
 */
public class ViewerTag extends WidgetTag {

    private Composite parent;
    private int style = SWT.NULL;

    /**
     * @param tagClass
     */
    public ViewerTag(final Class tagClass) {
        super(tagClass);
    }

    /**
     * @param tagClass
     * @param style
     */
    public ViewerTag(final Class tagClass, final int style) {
        super(tagClass);
        this.style = style;
    }

    /**
     * @return the visible viewer, if there is one.
     */
    public Viewer getViewer() {
        final Object bean = getBean();
        if (bean instanceof Viewer) {
            return (Viewer) bean;
        }
        return null;
    }

    /*
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    @Override
    protected Object newInstance(
        final Class theClass,
        final Map attributes,
        final XMLOutput output)
        throws JellyTagException {

        final int style = getStyle(attributes);

        // now lets call the constructor with the parent
        final Widget parent = getParentWidget();
        final Viewer viewer = (Viewer) createWidget(theClass, parent, style);

        return viewer;
    }

}
