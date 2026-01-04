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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A http session. This is the container for data shared across requests
 */
public class SessionTag extends TagSupport {

    /** Default host for requests */
    private String _host;

    /** Default port for requests */
    private String _port;

    /** Proxy details for requests */
    private Proxy _proxy = new Proxy();

    /** Whether the default is for secure comms */
    private boolean _secure;

    /** The browser identifier */
    private String _userAgent;

    /** Strict compliance */
    private boolean _strictMode = false;

    /** HTTP client used to store state and execute requests */
    private HttpClient _httpClient;

    /**
     * Creates a new instance of SessionTag
     */
    public SessionTag() {
    }

    /**
     * Process the tag
     *
     * @param xmlOutput to write output
     * @throws JellyTagException when any error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        if (_httpClient == null)
        {
            _httpClient = new HttpClient();
        }

        if (isProxyAvailable()) {
            _httpClient.getHostConfiguration().setProxy(getProxyHost(), getProxyPort());
        }

        invokeBody(xmlOutput);
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
     * Getter for property httpClient.
     *
     * @return Value of property httpClient.
     */
    public HttpClient getHttpClient() {
        return _httpClient;
    }

    /** Getter for property port.
     * @return Value of property port.
     */
    public String getPort() {
        return _port;
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
     * Helper method for proxy host property
     *
     * @return the {@link #getProxy() proxy's} host property
     */
    public String getProxyHost() {
        return getProxy().getHost();
    }

    /**
     * Helper method for proxy {@code port} property
     *
     * @return the {@link #getProxy() proxy's} port property
     */
    public int getProxyPort() {
        return getProxy().getPort();
    }

    /** Getter for property userAgent.
     * @return Value of property userAgent.
     */
    public String getUserAgent() {
        return _userAgent;
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
     * Getter for property secure.
     *
     * @return Value of property secure.
     */
    public boolean isSecure() {
        return _secure;
    }

    /** Getter for property strictMode.
     * @return Value of property strictMode.
     */
    public boolean isStrictMode() {
        return _strictMode;
    }

    /**
     * Setter for property host.
     *
     * @param host New value of property host.
     */
    public void setHost(final String host) {
        _host = host;
    }

    /**
     * Setter for property httpClient.
     *
     * @param httpClient New value of property httpClient.
     */
    public void setHttpClient(final HttpClient httpClient) {
        _httpClient = httpClient;
    }

    /** Setter for property port.
     * @param port New value of property port.
     */
    public void setPort(final String port) {
        _port = port;
    }

    /**
     * Setter for property proxy.
     *
     * @param proxy New value of property proxy.
     */
    public void setProxy(final Proxy proxy) {
        _proxy = proxy;
    }

    /**
     * Helper method for proxy {@code host} property
     *
     * @param host the {@link #getProxy() proxy's} host property
     */
    public void setProxyHost(final String host) {
        getProxy().setHost(host);
    }

    /**
     * Helper method for proxy {@code port} property
     *
     * @param port the {@link #getProxy() proxy's} port property
     */
    public void setProxyPort(final int port) {
        getProxy().setPort(port);
    }

    /**
     * Setter for property secure.
     *
     * @param secure New value of property secure.
     */
    public void setSecure(final boolean secure) {
        _secure = secure;
    }

    /** Setter for property strictMode.
     * @param strictMode New value of property strictMode.
     */
    public void setStrictMode(final boolean strictMode) {
        _strictMode = strictMode;
    }

    /** Setter for property userAgent.
     * @param userAgent New value of property userAgent.
     */
    public void setUserAgent(final String userAgent) {
        _userAgent = userAgent;
    }

}
