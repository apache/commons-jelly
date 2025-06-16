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

import java.util.StringTokenizer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.mortbay.http.handler.ResourceHandler;

/**
 * Declare a static file resource handler for a Jetty http server
 */
public class ResourceHandlerTag extends TagSupport {

    /** Parameter allowed methods */
    private String _allowedMethods;

    /** Creates a new instance of ResourceHandlerTag */
    public ResourceHandlerTag() {
    }

    /**
     * Perform the tag functionality. In this case, add a resource handler
     * to the parent context, setting the allowed methods if required
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final HttpContextTag httpContext = (HttpContextTag) findAncestorWithClass(
            HttpContextTag.class);
        if ( httpContext == null ) {
            throw new JellyTagException( "<resourceHandler> tag must be enclosed inside a <httpContext> tag" );
        }
        final ResourceHandler resourceHandler = new ResourceHandler();
        if (getAllowedMethods() != null) {
            // split comma-separated list up into tokens and convert to an array
            final StringTokenizer tokenizer =
                new StringTokenizer( getAllowedMethods(), " ," );
            final String[] allowedMethods = new String[tokenizer.countTokens()];
            for (int i = 0; i < allowedMethods.length; i++) {
                allowedMethods[i] = tokenizer.nextToken();
            }
            resourceHandler.setAllowedMethods(allowedMethods);
        }
        httpContext.addHandler(resourceHandler);
        invokeBody(xmlOutput);
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property allowedMethods.
     *
     * @return value of property allowedMethods.
     */
    public String getAllowedMethods() {
        return _allowedMethods;
    }

    /**
     * Setter for property allowedMethods.
     *
     * @param allowedMethods Comma separated list of allowed methods.
     */
    public void setAllowedMethods(final String allowedMethods) {
        _allowedMethods = allowedMethods;
    }

}
