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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SecurityConstraint.Authenticator;
import org.mortbay.util.Resource;

/**
 * Declare a context for a Jetty http server
 */
public class HttpContextTag extends TagSupport {

    /** Parameter path with default*/
    private String _contextPath = JettyHttpServerTag.DEFAULT_CONTEXT_PATH;

    /** Parameter resourceBase, with default */
    private String _resourceBase = JettyHttpServerTag.DEFAULT_RESOURCE_BASE;

    /** Parameter realmName*/
    private String _realmName;

    /** The actual context this tag refers to */
    private final HttpContext _context;

    /** Creates a new instance of HttpContextTag */
    public HttpContextTag() {
        // create an actual context for this tag
        _context = new HttpContext();
    }

    /**
     * Perform the tag functionality. In this case, setup the context path
     * and resource base before adding the context to the parent server
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {

        final JettyHttpServerTag httpserver = (JettyHttpServerTag) findAncestorWithClass(
            JettyHttpServerTag.class);
        if ( httpserver == null ) {
            throw new JellyTagException( "<httpContext> tag must be enclosed inside a <server> tag" );
        }

        // allow nested tags first, e.g body
        invokeBody(xmlOutput);

        _context.setContextPath(getContextPath());

        // convert the resource string to a URL
        // (this makes URL's relative to the location of the script
        try {
            final URL baseResourceURL = getContext().getResource(getResourceBase());
            _context.setBaseResource(Resource.newResource(baseResourceURL));
        }
        catch (final IOException e) {
            throw new JellyTagException(e);
        }

        if (null != getRealmName()) {
            _context.setRealmName(getRealmName());
        }
        httpserver.addContext(_context);

    }

    /**
     * Add an http handler to the context instance
     *
     * @param httpHandler the handler to add
     */
    public void addHandler(final HttpHandler httpHandler) {
        _context.addHandler(httpHandler);
    }

    /**
     * Add a security constraint for the specified path specification
     * to the context instance
     *
     * @param pathSpec the path specification for the security constraint
     * @param sc the security constraint to add
     */
    public void addSecurityConstraint(final String pathSpec, final SecurityConstraint sc) {
        _context.addSecurityConstraint(pathSpec, sc);
    }

    /**
     * Add an authenticator to the context instance
     *
     * @param authenticator the authenticator to add
     */
    public void setAuthenticator(final Authenticator authenticator)
    {
        _context.setAuthenticator(authenticator);
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------
    /**
     * Getter for property context path.
     *
     * @return value of property context path.
     */
    public String getContextPath() {
        return _contextPath;
    }

    /**
     * Setter for property context path.
     *
     * @param contextPath New resourceBase of property context path.
     */
    public void setContextPath(final String contextPath) {
        _contextPath = contextPath;
    }

    /**
     * Getter for property resourceBase.
     *
     * @return value of property resourceBase.
     */
    public String getResourceBase() {
        return _resourceBase;
    }

    /**
     * Setter for property resourceBase.
     *
     * @param resourceBase New value of property resourceBase.
     */
    public void setResourceBase(final String resourceBase) {
        _resourceBase = resourceBase;
    }

    /**
     * Getter for property realm name.
     *
     * @return value of property realm name.
     */
    public String getRealmName() {
        return _realmName;
    }

    /**
     * Setter for realm name.
     *
     * @param realmName New realm name.
     */
    public void setRealmName(final String realmName) {
        _realmName = realmName;
    }

}
