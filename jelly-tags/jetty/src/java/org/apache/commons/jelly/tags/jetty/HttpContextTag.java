/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/HttpContextTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 * $Revision: 1.3 $
 * $Date: 2002/07/14 12:38:22 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.jelly.tags.jetty;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SecurityConstraint.Authenticator;
import org.mortbay.util.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Declare a context for a Jetty http server
 *
 * @author  rtl
 * @version $Id: HttpContextTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 */
public class HttpContextTag extends TagSupport {

    /** parameter path with default*/
    private String _contextPath = JettyHttpServerTag.DEFAULT_CONTEXT_PATH;

    /** parameter resourceBase, with default */
    private String _resourceBase = JettyHttpServerTag.DEFAULT_RESOURCE_BASE;

    /** parameter realmName*/
    private String _realmName;

    /** the actual context this tag refers to */
    private HttpContext _context;

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
     * @throws Exception when an error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {

        JettyHttpServerTag httpserver = (JettyHttpServerTag) findAncestorWithClass(
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
            URL baseResourceURL = getContext().getResource(getResourceBase());
            _context.setBaseResource(Resource.newResource(baseResourceURL));
        } 
        catch (MalformedURLException e) {
            throw new JellyTagException(e);
        }
        catch (IOException e) {
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
     * @param handler the handler to add
     */
    public void addHandler(HttpHandler httHandler) {
        _context.addHandler(httHandler);
    }

    /**
     * Add a security constraint for the specified path specification
     * to the context instance
     *
     * @param pathSpec the path specification for the security constraint
     * @param sc the security constraint to add
     */
    public void addSecurityConstraint(String pathSpec, SecurityConstraint sc) {
        _context.addSecurityConstraint(pathSpec, sc);
    }

    /**
     * Add an authenticator to the context instance
     *
     * @param authenticator the authenticator to add
     */
    public void setAuthenticator(Authenticator authenticator)
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
     * @param path New resourceBase of property context path.
     */
    public void setContextPath(String contextPath) {
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
    public void setResourceBase(String resourceBase) {
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
     * Setter for property context path.
     *
     * @param path New resourceBase of property context path.
     */
    public void setRealmName(String realmName) {
        _realmName = realmName;
    }

}
