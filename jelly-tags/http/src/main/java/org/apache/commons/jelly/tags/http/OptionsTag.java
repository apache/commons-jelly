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
import org.apache.commons.httpclient.methods.OptionsMethod;

/**
 * A http get tag
 */
public class OptionsTag extends HttpTagSupport {

    /** The options method */
    private OptionsMethod _optionsMethod;

    /**
     * Creates a new instance of OptionsTag
     */
    public OptionsTag() {
    }

    /**
     * @return a url method for an options request
     * @throws MalformedURLException when the url is bad
     */
    @Override
    protected HttpMethod getHttpMethod() throws MalformedURLException {
        if (_optionsMethod == null) {
            _optionsMethod = new OptionsMethod(getResolvedUrl());
        }
        return _optionsMethod;
    }

}
