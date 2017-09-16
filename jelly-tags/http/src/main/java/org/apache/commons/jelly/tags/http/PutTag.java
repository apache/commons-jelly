/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.http;

import java.net.MalformedURLException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PutMethod;


/**
 * A http put
 *
 * @author  dion
 * @version $Id$
 */
public class PutTag extends HttpTagSupport {

    /** the put method */
    private PutMethod _putMethod;

    /** Creates a new instance of PutTag */
    public PutTag() {
    }

    /**
     * Return a {@link HttpMethod method} to be used for put'ing
     *
     * @return a HttpMethod implementation
     * @throws MalformedURLException when the {@link getUrl() url} or
     * {@link #getPath() path} is invalid
     */
    protected HttpMethod getHttpMethod() throws MalformedURLException {
        if (_putMethod == null) {
            _putMethod = new PutMethod(getResolvedUrl());
        }
        return _putMethod;
    }

    /**
     * Set the current parameters on the url method ready for processing
     *
     */
    protected void setParameters() {
    }

    /**
     * Fail as PUT requests don't have parameters
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void addParameter(String name, String value) {
        throw new IllegalArgumentException("PUT requests don't have params");
    }

}
