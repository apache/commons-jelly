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
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.DynamicTagLibrary;

/**
 * &lt;extend&gt; is used to extend a dynamic tag defined in an inherited
 * dynamic tag library.
 * @see SuperTag
 */
public class ExtendTag extends DefineTagSupport {

    private String name;

    private Script superScript;

    public ExtendTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final DynamicTagLibrary library = getTagLibrary();
        final DynamicTagLibrary owner = library.find(getName());
        if (owner == null) {
            throw new JellyTagException(
                "Cannot extend " + getName() + ": dynamic tag not defined");
        }
        if (owner == library) {
            // disallow extension of tags defined within the same tag
            // library
            throw new JellyTagException("Cannot extend " + getName() +
                                     ": dynamic tag defined locally");
        }
        superScript = owner.getDynamicTag(name);
        if (superScript == null) {
            // tag doesn't define a script - disallow this for the moment.
            throw new JellyTagException("Cannot extend " + getName() +
                                     ": tag is not a dynamic tag");
        }

        owner.registerDynamicTag(getName() , getBody());
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the name of the tag to create
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the parent implementation of this tag
     */
    public Script getSuperScript() {
        return superScript;
    }

    /**
     * Sets the name of the tag to create
     */
    public void setName(final String name) {
        this.name = name;
    }
}

