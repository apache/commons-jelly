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
package org.apache.commons.jelly.tags.xml;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/** Sets a parameter in the parent transform tag
  */
public class ParamTag extends TagSupport {

    /** The name of the attribute. */
    private String name;

    /** The value of the attribute. */
    private Object value;

    public ParamTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final TransformTag tag = (TransformTag) this.findAncestorWithClass( TransformTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "<param> tag must be enclosed inside a <transform> tag" );
        }
        Object value = this.getValue();
        if (value == null) {
            value = getBodyText();
        }

        tag.setParameterValue( getName(), value );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the name of the attribute.
     */
    public String getName() {
        return name;
    }
    /**
     * @return the value of the attribute.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the name of the attribute
     */
    public void setName(final String name) {
        this.name = name;
    }
    /**
     * Sets the value of the attribute
     */
    public void setValue(final Object value) {
        this.value = value;
    }
}
