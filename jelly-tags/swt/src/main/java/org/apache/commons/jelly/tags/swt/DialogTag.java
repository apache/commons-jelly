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

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * This tag creates an SWT dialog.
 */
public class DialogTag extends WidgetTag {

    /**
     * @param widgetClass
     */
    public DialogTag(final Class widgetClass) {
        super(widgetClass);
    }

    /**
     * @param widgetClass
     * @param style
     */
    public DialogTag(final Class widgetClass, final int style) {
        super(widgetClass, style);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Factory method to create a new dialog
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output)
        throws JellyTagException {
        final int style = getStyle(attributes);

        // now lets call the constructor with the parent
        final Widget parent = getParentWidget();

        final boolean isParentShell = parent instanceof Shell;
        if (parent == null || !isParentShell) {
            throw new JellyTagException("This tag must be nested within a Shell");
        }

        final Dialog dialog = (Dialog) createWidget(theClass, parent, style);

        return dialog;
    }

}
