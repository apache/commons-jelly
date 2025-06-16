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

import javax.swing.border.Border;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An abstract base class used for concrete border tags which create new Border implementations
 * and sets then on parent widgets and optionally export them as variables.
 */
public abstract class BorderTagSupport extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BorderTagSupport.class);

    private String var;

    public BorderTagSupport() {
    }

    /**
     * Factory method to create a new Border instance.
     */
    protected abstract Border createBorder();

    // Properties
    //-------------------------------------------------------------------------

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        final Border border = createBorder();

        // allow some nested tags to set properties
        invokeBody(output);

        if (var != null) {
            context.setVariable(var, border);
        }
        final ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag != null ) {
            tag.setBorder(border);
        } else if (var == null) {
            throw new JellyTagException( "Either the 'var' attribute must be specified to export this Border or this tag must be nested within a JellySwing widget tag" );
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the variable to use to expose the new Border object.
     * If this attribute is not set then the parent widget tag will have its
     * border property set.
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
