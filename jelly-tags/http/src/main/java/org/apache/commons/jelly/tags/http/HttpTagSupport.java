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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * The base tag for all http requests
 */
public abstract class HttpTagSupport extends TagSupport {

    /** The header name for the user agent */
    private static final String HEADER_NAME_USER_AGENT = "User-Agent";

    /** Unique identifier of the tag/ variable to store result in */
    private String _var;

    /**
     * the path to be tested relative to the host/port specifed on the parent
     * {@link SuiteTag suite tag}.
     * Either this property and the suite's host
     * must be provided, or the {@link #getUrl() url} property must be specifed
     */
    private String _path;

    /**
     * The complete uri to be processed
     * Either this property or the {@link #getPath() path} and the suite's host
     * must be provided.
     */
    private String _uri;

    /** Whether or not to follow redirects */
    private boolean _followRedirects = false;

    /** List of parameters as name value pairs */
    private List _parameters;

    /** List of headers as name value pairs */
    private List _requestHeaders;

    /**
     * Creates a new instance of HttpTag
     */
    public HttpTagSupport() {
        setParameters(new ArrayList());
        setRequestHeaders(new ArrayList());
    }

    /**
     * Add a parameter to the list
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void addParameter(final String name, final String value) {
        getParameters().add(new NameValuePair(name, value));
    }

    /**
     * Add a request header to the list
     *
     * @param name the header name
     * @param value the header value
     */
    public void addRequestHeader(final String name, final String value) {
        getRequestHeaders().add(new NameValuePair(name, value));
    }

    /**
     * Perform the tag functionality. In this case, get the http url method
     * execute it and make it available for validation
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        // allow nested tags first, e.g body
        invokeBody(xmlOutput);

        // track request execution
        final long start = System.currentTimeMillis();
        HttpMethod urlMethod = null;
        try {
            urlMethod = getConfiguredHttpMethod();
            getHttpClient().executeMethod(urlMethod);
        }
        catch (final IOException e) {
            throw new JellyTagException(e);
        }
        final long end = System.currentTimeMillis();

        // set variable to value
        if (getVar() != null) {
            getContext().setVariable(getVar(), urlMethod);
            getContext().setVariable(getVar() + ".responseTime",
                String.valueOf(end - start));
        }
    }

    /**
     * retrieve the {@link HttpUrlMethod method} from the subclass and
     * configure it ready for execution
     *
     * @return a configured {@link HttpUrlMethod method}
     * @throws MalformedURLException when retrieving the URL fails
     */
    private HttpMethod getConfiguredHttpMethod() throws
    MalformedURLException {
        // retrieve and configure url method
        final HttpMethod urlMethod = getHttpMethod();
        urlMethod.setFollowRedirects(isFollowRedirects());
        // add request headers
        NameValuePair header = null;
        for (final Object element : getRequestHeaders()) {
            header = (NameValuePair) element;
            urlMethod.addRequestHeader(header.getName(), header.getValue());
        }
        // add parameters
        setParameters(urlMethod);
        // add the default user agent to the list if one doesn't exist
        // and the session tag does exist and have a user agent
        if (urlMethod.getRequestHeader(HttpTagSupport.HEADER_NAME_USER_AGENT)
            == null && getSessionTag() != null
            && getSessionTag().getUserAgent() != null) {

            urlMethod.addRequestHeader(HttpTagSupport.HEADER_NAME_USER_AGENT,
                    getSessionTag().getUserAgent());
        }
        return urlMethod;
    }

    /**
     * return a HttpClient shared on the session tag, or a new one if no
     * session tag exists
     *
     * @return the shared http client from the session tag, or create a new one.
     */
    private HttpClient getHttpClient() {
        final SessionTag session = getSessionTag();
        HttpClient client = null;
        if (session != null) {
            client = session.getHttpClient();
            client.setStrictMode(session.isStrictMode());
        } else {
            client = new HttpClient();
        }
        return client;
    }

    /**
     * A method that must be implemented by subclasses to provide the
     * {@link HttpMethod url method} implementation
     *
     * @return a HttpUrlMethod implementation
     * @throws MalformedURLException when the {@link #getUri() uri} or
     * {@link #getPath() path} is invalid
     */
    protected abstract HttpMethod getHttpMethod()
        throws MalformedURLException;

    /**
     * Getter for property parameters.
     *
     * @return Value of property parameters.
     */
    public List getParameters() {
        return _parameters;
    }

    /**
     * Getter for property path.
     *
     * @return Value of property path.
     */
    public String getPath() {
        return _path;
    }

    /**
     * Getter for property requestHeaders.
     *
     * @return Value of property requestHeaders.
     */
    public List getRequestHeaders() {
        return _requestHeaders;
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * @return the url specified by the tag, either the url if not null, or
     * a combination of the host, port and path
     */
    public String getResolvedUrl() {
        if (getUri() != null) {
            return getUri();
        }
        // build it from path, host and optionally port
        final SessionTag session = (SessionTag) findAncestorWithClass(
            SessionTag.class);
        final String host = session.getHost();
        final String port = session.getPort();
        // short term hack, need to add port and security in
        return "http://" + host + getPath();
    }

    /**
     * retrieve the optional parent session tag
     *
     * @return the ancestor tag with class {@link SessionTag} or null if
     *      not found
     */
    private SessionTag getSessionTag() {
        final SessionTag sessionTag = (SessionTag) findAncestorWithClass(
            SessionTag.class);
        return sessionTag;
    }

    /**
     * Getter for property uri.
     *
     * @return Value of property uri.
     */
    public String getUri() {
        return _uri;
    }

    /**
     * Getter for property var.
     *
     * @return Value of property var.
     */
    public String getVar() {
        return _var;
    }

    /**
     * Getter for property followRedirects.
     *
     * @return Value of property followRedirects.
     */
    public boolean isFollowRedirects() {
        return _followRedirects;
    }

    /**
     * Setter for property followRedirects.
     *
     * @param followRedirects New value of property followRedirects.
     */
    public void setFollowRedirects(final boolean followRedirects) {
        _followRedirects = followRedirects;
    }

    /**
     * Sets the current parameters on the url method ready for processing
     *
     * @param method the {@link HttpMethod method} to configure
     * @throws MalformedURLException Never thrown here but can be from a subclass.
     */
    protected void setParameters(final HttpMethod method) throws MalformedURLException {
        if (getParameters().size() > 0) {
            final NameValuePair[] parameters = (NameValuePair[]) getParameters().
                toArray(new NameValuePair[0]);
            method.setQueryString(parameters);
        }
    }

    /**
     * Setter for property parameters.
     *
     * @param parameters New value of property parameters.
     */
    public void setParameters(final List parameters) {
        _parameters = parameters;
    }

    /**
     * Setter for property path.
     *
     * @param path New value of property path.
     */
    public void setPath(final String path) {
        _path = path;
    }

    /**
     * Setter for property requestHeaders.
     *
     * @param requestHeaders New value of property requestHeaders.
     */
    public void setRequestHeaders(final List requestHeaders) {
        _requestHeaders = requestHeaders;
    }

    /**
     * Setter for property uri.
     *
     * @param uri New value of property uri.
     */
    public void setUri(final String uri) {
        _uri = uri;
    }

    /**
     * Setter for property var.
     *
     * @param var New value of property var.
     */
    public void setVar(final String var) {
        _var = var;
    }

}
