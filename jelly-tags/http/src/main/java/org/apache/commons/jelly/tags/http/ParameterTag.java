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

package org.apache.commons.jelly.tags.http;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag to hold parameters
 */
public class ParameterTag extends TagSupport {
    /** Parameter name */
    private String _name;
    /** Parameter value */
    private String _value;

    /** Creates a new instance of ParameterTag */
    public ParameterTag() {
    }

    /**
     * Perform the tag functionality. In this case, simply evaluate the body.
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final HttpTagSupport http = (HttpTagSupport) findAncestorWithClass(
            HttpTagSupport.class);
        http.addParameter(getName(), getValue());
        invokeBody(xmlOutput);
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------
    /**
     * Getter for property name.
     *
     * @return Value of property name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Getter for property value.
     *
     * @return Value of property value.
     */
    public String getValue() {
        return _value;
    }

    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     */
    public void setName(final String name) {
        _name = name;
    }

    /**
     * Setter for property value.
     *
     * @param value New value of property value.
     */
    public void setValue(final String value) {
        _value = value;
    }

}
