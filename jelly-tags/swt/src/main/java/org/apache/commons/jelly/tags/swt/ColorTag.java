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
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.swt.converters.ColorConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;

/**
 * Class to create a {@link Color} instance within Jelly SWT.
 */
public class ColorTag extends TagSupport {

    /** RGB value */
    private String rgb;

    /** Variable name */
    private String var;

    /**
     * Creates a {@link Color} instance as defined by the RGB attribute.
     * Stores this {@link Color} instance in the Context so that it can be
     * referenced in the Jelly script.
     *
     * @param output {@link XMLOutput} reference
     * @throws JellyTagException if an error occurs
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // invoke by body just in case some nested tag configures me
        invokeBody(output);

        final Widget parent = getParentWidget();

        if (parent == null) {
            throw new JellyTagException(
                "This tag must be nested within a Widget or a Window"
            );
        }

        if (var == null) {
            throw new JellyTagException("This tag requires a context variable name");
        }

        final Color color =
            new Color(
                parent.getDisplay(),
                ColorConverter.getInstance().parse(getRgb())
            );

        // store the Color in the context
        context.setVariable(var, color);
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

    /**
     * Obtain the RGB value for this {@link Color} instance
     *
     * @return the RGB value (eg. #666666)
     */
    public String getRgb() {
        return this.rgb;
    }

    /**
     * Obtain the variable name.
     *
     * @return the variable name of this {@link Color} instance
     */
    public String getVar() {
        return this.var;
    }

    /**
     * Sets the RGB value for this {@link Color} instance
     *
     * @param rgb value (eg. #666666);
     */
    public void setRgb(final String rgb) {
        this.rgb = rgb;
    }

    // Tag interface
    //-------------------------------------------------------------------------

    /**
     * Sets the variable name
     *
     * @param var the variable name of this {@link Color} instance
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
