/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/SessionTag.java,v 1.8 2002/07/14 12:38:22 dion Exp $
 * $Revision: 1.8 $
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

package org.apache.commons.jelly.tags.http;

import org.apache.commons.httpclient.HttpMultiClient;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A http session. This is the container for data shared across requests
 *
 * @author dion
 */
public class SessionTag extends TagSupport {
    
    /** default host for requests */
    private String _host;
    /** default port for requests */
    private String _port;
    /** Proxy details for requests */
    private Proxy _proxy = new Proxy();
    /** whether the default is for secure comms */
    private boolean _secure;
    /** the browser identifier */
    private String _userAgent;
    /** strict compliance */
    private boolean _strictMode = false;
    
    /** http client used to store state and execute requests */
    private HttpMultiClient _httpClient;
    
    /** 
     * Creates a new instance of SessionTag
     */
    public SessionTag() {
    }
    
    /**
     * Process the tag
     *
     * @param xmlOutput to write output
     * @throws Exception when any error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws Exception {
        if (isProxyAvailable()) {
            _httpClient = new HttpMultiClient(getProxyHost(), getProxyPort());
        } else {
            _httpClient = new HttpMultiClient();
        }
        
        invokeBody(xmlOutput);
    }
    
    /**
     * Getter for property httpClient.
     *
     * @return Value of property httpClient.
     */
    public HttpMultiClient getHttpClient() {
        return _httpClient;
    }
    
    /**
     * Setter for property httpClient.
     *
     * @param httpClient New value of property httpClient.
     */
    public void setHttpClient(HttpMultiClient httpClient) {
        _httpClient = httpClient;
    }
    /**
     * Tests whether the {@link #getProxy() proxy} is ready for use
     *
     * @return true if the {@link #getProxy() proxy} is configured for use
     */
    public boolean isProxyAvailable() {
        return getProxy() != null && getProxy().getHost() != null 
            && getProxy().getPort() != Proxy.PORT_UNSPECIFIED;
    }
    
    /**
     * Helper method for proxy host property
     *
     * @return the {@link #getProxy() proxy's} host property
     */
    public String getProxyHost() {
        return getProxy().getHost();
    }
    
    /**
     * Helper method for proxy <code>host</code> property
     *
     * @param host the {@link #getProxy() proxy's} host property
     */
    public void setProxyHost(String host) {
        getProxy().setHost(host);
    }
    
    /**
     * Helper method for proxy <code>port</code> property
     *
     * @return the {@link #getProxy() proxy's} port property
     */
    public int getProxyPort() {
        return getProxy().getPort();
    }
    
    /**
     * Helper method for proxy <code>port</code> property
     *
     * @param port the {@link #getProxy() proxy's} port property
     */
    public void setProxyPort(int port) {
        getProxy().setPort(port);
    }
    
    /**
     * Getter for property host.
     *
     * @return Value of property host.
     */
    public String getHost() {
        return _host;
    }
    
    /**
     * Setter for property host.
     *
     * @param host New value of property host.
     */
    public void setHost(String host) {
        _host = host;
    }
    
    /** Getter for property port.
     * @return Value of property port.
     */
    public String getPort() {
        return _port;
    }
    
    /** Setter for property port.
     * @param port New value of property port.
     */
    public void setPort(String port) {
        _port = port;
    }
    
    /**
     * Getter for property proxy.
     *
     * @return Value of property proxy.
     */
    public Proxy getProxy() {
        return _proxy;
    }
    
    /**
     * Setter for property proxy.
     *
     * @param proxy New value of property proxy.
     */
    public void setProxy(Proxy proxy) {
        _proxy = proxy;
    }
    
    /**
     * Getter for property secure.
     *
     * @return Value of property secure.
     */
    public boolean isSecure() {
        return _secure;
    }
    
    /**
     * Setter for property secure.
     *
     * @param secure New value of property secure.
     */
    public void setSecure(boolean secure) {
        _secure = secure;
    }

    /** Getter for property userAgent.
     * @return Value of property userAgent.
     */
    public String getUserAgent() {
        return _userAgent;
    }
    
    /** Setter for property userAgent.
     * @param userAgent New value of property userAgent.
     */
    public void setUserAgent(String userAgent) {
        _userAgent = userAgent;
    }
    
    /** Getter for property strictMode.
     * @return Value of property strictMode.
     */
    public boolean isStrictMode() {
        return _strictMode;
    }
    
    /** Setter for property strictMode.
     * @param strictMode New value of property strictMode.
     */
    public void setStrictMode(boolean strictMode) {
        _strictMode = strictMode;
    }
    
}
