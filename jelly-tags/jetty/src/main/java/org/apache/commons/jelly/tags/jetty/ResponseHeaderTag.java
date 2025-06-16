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

package org.apache.commons.jelly.tags.jetty;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.mortbay.http.HttpResponse;

/**
 * Sets a response header in the request handler of a Jetty http server
 */
public class ResponseHeaderTag extends TagSupport {

    /** Parameter name */
    private String _name;

    /** Parameter value */
    private String _value;

    /**
     * Perform the tag functionality. In this case, set a header in the
     * http response found in the jelly context
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {

        if (null == getName()) {
            throw new JellyTagException("<responseHeader> tag must have a name");
        }

        // get the response from the context
        final HttpResponse httpResponse = (HttpResponse) getContext().getVariable("response");
        if (null == httpResponse) {
            throw new JellyTagException("HttpResponse variable not available in Jelly context");
        }

        // if value is valid then set it
        // otherwise remove the field
        if (null != getValue()) {
            httpResponse.setField(getName(), getValue());
        } else {
            httpResponse.removeField(getName());
        }

    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property context path.
     *
     * @return value of property context path.
     */
    public String getName() {
        return _name;
    }

    /**
     * Getter for property value.
     *
     * @return value of property value.
     */
    public String getValue() {
        return _value;
    }

    /**
     * Setter for property context path.
     *
     * @param name New value of property context path.
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

