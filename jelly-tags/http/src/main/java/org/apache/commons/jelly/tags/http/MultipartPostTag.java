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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * A Multipart MIME message post
 *
 * This tag should contain one or more &lt;part&gt; tags, to
 * specify the multiple parts of the message
 *
 * Example:
 * <pre>
 *   &lt;mppost uri="http://localhost?doit"&gt;
 *       &lt;part name="user" type="text/plain"&gt;Fred&lt;/part&gt;
 *       &lt;part name="data"  type="text/plain"&gt;This is the second part of the message&lt;/part&gt;
 *   &lt;/mppost&gt;
 *</pre>
 * @since ???
 */
public class MultipartPostTag extends PostTag {

    /** The post method */
    private MultipartPostMethod _postMethod;

    /** List of parts as name value pairs */
    private List _parts;

    /** Creates a new instance of MppostTag */
    public MultipartPostTag() {
      _parts = new ArrayList();
    }

    /**
     * Gets a {@link HttpMethod method} to be used for multi-part post'ing
     *
     * @return a HttpUrlMethod implementation
     * @throws MalformedURLException when the {@link #getUri() URI} or
     * {@link #getPath() path} is invalid
     */
    @Override
    protected HttpMethod getHttpMethod() throws MalformedURLException {
        if (_postMethod == null) {
            _postMethod = new MultipartPostMethod(getResolvedUrl());
        }
        return _postMethod;
    }

    /**
     * Add a part to the message
     *
     * @param part the part
     */
    public void addPart(Part part) {
        _parts.add(part);
    }

    /**
     * Sets the current parameters on the url method ready for processing
     *
     * This method basically
     * It <strong>must</strong> be called after
     *  {@link #getHttpMethod()}
     */
    @Override
    protected void setParameters(HttpMethod method) {
        for (int index = 0; index < _parts.size(); index++) {
            ((MultipartPostMethod) method).addPart( (Part) _parts.get(index) );
        }
    }
}
