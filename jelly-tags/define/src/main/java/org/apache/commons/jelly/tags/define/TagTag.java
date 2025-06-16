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
import org.apache.commons.jelly.XMLOutput;

/**
 * &lt;tag&gt; is used to define a new tag
 * using a Jelly script to implement the behavior of the tag.
 * <p>
 * Parameters can be passed into the new tag using normal XML attribute
 * notations. Inside the body of the tag definition, the attributes can
 * be accessed as normal Jelly variables.
 * </p>
 */
public class TagTag extends DefineTagSupport {

    private String name;

    public TagTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        getTagLibrary().registerDynamicTag( getName(), getBody() );
    }

    // Properties
    //-------------------------------------------------------------------------

    /** @return the name of the tag to create */
    public String getName() {
        return name;
    }

    /** Sets the name of the tag to create */
    public void setName(final String name) {
        this.name = name;
    }
}
