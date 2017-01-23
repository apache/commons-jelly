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

package org.apache.commons.jelly.tags.jetty;

import java.util.Map;

import org.apache.commons.jelly.TagLibrary;

/**
 * A set of jelly tags for instantiating a Jetty HTTP server
 *
 * @author rtl
 * @version $Id$
 */
public class JettyTagLibrary extends TagLibrary {

    /**
     * Creates a new instance of LatkaTagLibrary
     */
    public JettyTagLibrary() {

        registerTag("jettyHttpServer", JettyHttpServerTag.class);
        registerTag("socketListener", SocketListenerTag.class);
        registerTag("realm", RealmTag.class);
        registerTag("httpContext", HttpContextTag.class);
        registerTag("resourceHandler", ResourceHandlerTag.class);
        registerTag("notFoundHandler", NotFoundHandlerTag.class);
        registerTag("securityHandler", SecurityHandlerTag.class);

        registerTag("jellyResourceHandler", JellyResourceHandlerTag.class);
        registerTag("getRequest", GetRequestTag.class);
        registerTag("postRequest", PostRequestTag.class);
        registerTag("putRequest", PutRequestTag.class);
        registerTag("deleteRequest", DeleteRequestTag.class);
        registerTag("responseHeader", ResponseHeaderTag.class);
        registerTag("responseBody", ResponseBodyTag.class);
        registerTag("responseCode", ResponseCodeTag.class);
    }

    /**
     * @see TagLibrary#getTagClasses()
     *
     * @return a Map of tag name to tag class
     */
    public Map getTagClasses() {
        return super.getTagClasses();
    }

}

