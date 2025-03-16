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
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * A http get tag
 */
public class GetTag extends HttpTagSupport {

    /** The get method */
    private GetMethod _getMethod;

    /**
     * Creates a new instance of GetTag
     */
    public GetTag() {
    }

    /**
     * @return a url method for a get request
     * @throws MalformedURLException when the url is bad
     */
    @Override
    protected HttpMethod getHttpMethod() throws MalformedURLException {
        if (_getMethod == null) {
            _getMethod = new GetMethod(getResolvedUrl());
        }
        return _getMethod;
    }

}
