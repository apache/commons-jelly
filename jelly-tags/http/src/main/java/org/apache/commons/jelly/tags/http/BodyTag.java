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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag to set the body for posts and puts etc
 */
public class BodyTag extends TagSupport {

    /** Creates a new instance of BodyTag */
    public BodyTag() {
    }

    /**
     * Perform the tag functionality. In this case, get the parent http tag,
     * and if it's a post or put, set the request body from the body of this
     * tag.
     *
     * @param xmlOutput for writing output to
     * @throws JellyTagException when any error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final HttpTagSupport httpTag = (HttpTagSupport) findAncestorWithClass(
            HttpTagSupport.class);

        HttpMethod httpMethod = null;
        try {
            httpMethod = httpTag.getHttpMethod();
        } catch (final MalformedURLException e) {
            throw new JellyTagException(e);
        }

        final String bodyText = getBodyText();
        if (httpMethod instanceof PostMethod) {
            final PostMethod postMethod = (PostMethod) httpMethod;
            postMethod.setRequestEntity(new StringRequestEntity(bodyText));
        } else if (httpMethod instanceof PutMethod) {
            final PutMethod putMethod = (PutMethod) httpMethod;
            putMethod.setRequestEntity(new StringRequestEntity(bodyText));
        } else {
            throw new IllegalStateException("Http method from parent was "
                + "not post or put");
        }
    }

}
