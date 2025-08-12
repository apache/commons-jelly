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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowImpl;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.jelly.tags.swt.LayoutTag;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * Implementation of SWT LayoutTag
 */
public class JFaceLayoutTag extends LayoutTag {

    /**
     * @param layoutClass
     */
    public JFaceLayoutTag(final Class layoutClass) {
        super(layoutClass);
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the parent window
     */
    public Window getParentWindow() {
        final ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);
        if (tag != null) {
            return tag.getWindow();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#processBean(String, Object)
     */
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {

        Widget parent = getParentWidget();
        if (parent == null) { // perhaps parent is a Window
            final Window window = getParentWindow();
            if (window != null && window instanceof ApplicationWindowImpl) {
                parent = ((ApplicationWindowImpl) window).getContents();
            }
        }

        if (!(parent instanceof Composite)) {
            throw new JellyTagException("This tag must be nested within a composite widget tag");
        }
        final Composite composite = (Composite) parent;
        composite.setLayout(getLayout());

    }
}

