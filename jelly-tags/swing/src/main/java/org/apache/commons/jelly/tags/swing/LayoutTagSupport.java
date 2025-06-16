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

import java.awt.Component;
import java.awt.LayoutManager;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An abstract base class used for concrete layout tags which create new LayoutManager implementations
 * and either export them as variables or set them on parent widgets.
 */
public abstract class LayoutTagSupport extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTagSupport.class);

    private String var;

    public LayoutTagSupport() {
    }

    /**
     * Adds the given layout component to the container with the specified constraints
     */
    public void addLayoutComponent(final Component component, final Object constraints) throws JellyTagException {
        getComponentTag().addChild(component, constraints);
    }

    /**
     * Factory method to create a new LayoutManager instance.
     */
    protected abstract LayoutManager createLayoutManager();

    // Properties
    //-------------------------------------------------------------------------

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        final LayoutManager layout = createLayoutManager();

        if (var != null) {
            context.setVariable(var, layout);
        }

        getComponentTag().setLayout(layout);

        // allow some nested tags to set properties
        invokeBody(output);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * @return the parent component tag or throw an exception
     */
    protected ComponentTag getComponentTag() throws JellyTagException {
        final ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag must be nested within a JellySwing widget tag" );
        }
        return tag;
    }

    /**
     * Sets the name of the variable to use to expose the new LayoutManager object.
     * If this attribute is not set then the parent widget tag will have its
     * layout property set.
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
