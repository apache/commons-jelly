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

import java.awt.BorderLayout;
import java.awt.Component;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Represents a layout of a child component within its parent &lt;borderLayout&gt; layout.
 */
public class BorderAlignTag extends TagSupport implements ContainerTag {

    private String align;

    // ContainerTag interface
    //-------------------------------------------------------------------------

    /**
     * Adds a child component to this parent
     */
    @Override
    public void addChild(final Component component, final Object constraints) throws JellyTagException {
        final BorderLayoutTag tag = (BorderLayoutTag) findAncestorWithClass( BorderLayoutTag.class );
        if (tag == null) {
            throw new JellyTagException( "this tag must be nested within a <borderLayout> tag" );
        }
        tag.addLayoutComponent(component, getConstraints());
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the align.
     * @return String
     */
    public String getAlign() {
        return align;
    }

    protected Object getConstraints() {
        if ("north".equalsIgnoreCase(align)) {
            return BorderLayout.NORTH;
        }
        if ("south".equalsIgnoreCase(align)) {
            return BorderLayout.SOUTH;
        }
        if ("east".equalsIgnoreCase(align)) {
            return BorderLayout.EAST;
        }
        if ("west".equalsIgnoreCase(align)) {
            return BorderLayout.WEST;
        }
        // default to CENTER
        return BorderLayout.CENTER;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the alignment of the child component which is a case insensitive value
     * of {NORTH, SOUTH, EAST, WEST, CENTER} which defaults to CENTER
     */
    public void setAlign(final String align) {
        this.align = align;
    }
}

