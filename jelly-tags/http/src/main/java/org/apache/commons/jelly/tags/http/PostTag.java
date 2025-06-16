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

import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * An HTTP POST.
 */
public class PostTag extends HttpTagSupport {

    /** The post method */
    private PostMethod _postMethod;

    /** Creates a new instance of PostTag */
    public PostTag() {
    }

    /**
     * Gets a {@link HttpMethod method} to be used for post'ing
     *
     * @return a HttpUrlMethod implementation
     * @throws MalformedURLException when the {@link #getUri() URI} or
     * {@link #getPath() path} is invalid
     */
    @Override
    protected HttpMethod getHttpMethod() throws MalformedURLException {
        if (_postMethod == null) {
            _postMethod = new PostMethod(getResolvedUrl());
        }
        return _postMethod;
    }

    /**
     * Sets the current parameters on the URL method ready for processing
     *
     * This method <strong>must</strong> be called after
     *  {@link #getHttpMethod()}
     */
    @Override
    protected void setParameters(final HttpMethod method) {
        final NameValuePair nvp = null;
        for (final Object element : getParameters()) {
            final NameValuePair parameter = (NameValuePair) element;
            ((PostMethod) method).addParameter(parameter);
        }
    }

}
