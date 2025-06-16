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
import java.net.URL;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.UserRealm;
import org.mortbay.http.handler.NotFoundHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.Log;
import org.mortbay.util.MultiException;
import org.mortbay.util.OutputStreamLogSink;
import org.mortbay.util.Resource;

/**
 * Declare an instance of a Jetty http server
 */
public class JettyHttpServerTag extends TagSupport {

    /** Default port to create listeners for */
    public static final int DEFAULT_PORT = 8100;

    /** Default host to create listeners/context for */
    public static final String DEFAULT_HOST = "localhost";

    /** Default context to create context for */
    public static final String DEFAULT_CONTEXT_PATH = "/";

    /** Default resource base to use for context */
    public static final String DEFAULT_RESOURCE_BASE = "./docRoot";

    /** Default log file for Jetty */
    public static final String DEFAULT_LOG_FILE = "jetty.log";

    /** The Log to which logging calls will be made. */
    private static final org.apache.commons.logging.Log log =
        LogFactory.getLog(JettyHttpServerTag.class);

    /** The log sink for the Jety server */
    private static OutputStreamLogSink _logSink;

    // static initialization
    {
        // setup a log for Jetty with a default file name
        try {
            _logSink = new OutputStreamLogSink(DEFAULT_LOG_FILE);
            //_logSink.start();
            Log.instance().add(_logSink);
        } catch (final Exception ex ) {
            log.error(ex.getLocalizedMessage());
        }

    }

    /** Unique identifier of the tag/ variable to store result in */
    private String _var;

    /** The HTTP server for this tag */
    private final HttpServer _server;

    /** File name of Jetty log file - with default */
    private String _logFileName = DEFAULT_LOG_FILE;

    /** Creates a new instance of JettyHttpServerTag */
    public JettyHttpServerTag() {

        // Create the server
        _server=new HttpServer();

        // turn off alias checking in Jetty's FileResource,
        // so that we don't need exact case in resource names
        System.setProperty("org.mortbay.util.FileResource.checkAliases", "false");
    }

    /**
     * Add an http context to the server instance
     *
     * @param context the context to add
     */
    public void addContext(final HttpContext context) {
        _server.addContext(context);
    }

    /**
     * Add an http listener to the server instance
     *
     * @param listener the listener to add
     */
    public void addListener(final HttpListener listener) {
        _server.addListener(listener);
    }

    /**
     * Add a user authentication realm to the server instance
     *
     * @param realm the realm to add
     * @return the realm added
     */
    public UserRealm addRealm(final UserRealm realm)
    {
        return _server.addRealm(realm);
    }

    /**
     * Perform the tag functionality. In this case, create an http server after
     * making sure that it has at least one context and associated http handler,
     * creating defaults if it doesn't
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {

        try {
            final URL logFileURL = getContext().getResource(getLogFileName());
            _logSink.setFilename(logFileURL.getPath());
            _logSink.start();
        } catch (final Exception ex ) {
            log.error(ex.getLocalizedMessage());
        }

        // allow nested tags first, e.g body
        invokeBody(xmlOutput);

        try {
            // if no listeners create a default port listener
            if (_server.getListeners().length == 0) {
                final SocketListener listener=new SocketListener();
                listener.setPort(DEFAULT_PORT);
                listener.setHost(DEFAULT_HOST);
                _server.addListener(listener);
            }

            // if no context/s create a default context
            if (_server.getContexts().length == 0) {
                log.info("Creating a default context");
                // Create a context
                final HttpContext context = _server.getContext(DEFAULT_HOST,
                                                        DEFAULT_CONTEXT_PATH);

                // Serve static content from the context
                final URL baseResourceURL = getContext().getResource(DEFAULT_RESOURCE_BASE);
                final Resource resource = Resource.newResource(baseResourceURL);
                context.setBaseResource(resource);
                _server.addContext(context);
            }
        }
        catch (final IOException e) {
            throw new JellyTagException(e);
        }

        // check that all the contexts have at least one handler
        // if not then add a default resource handler and a not found handler
        final HttpContext[] allContexts = _server.getContexts();
        for (final HttpContext currContext : allContexts) {
            if (currContext.getHandlers().length == 0) {
                log.info("Adding resource and not found handlers to context:" +
                         currContext.getContextPath());
                currContext.addHandler(new ResourceHandler());
                currContext.addHandler(new NotFoundHandler());
            }
        }

        // Start the http server
        try {
            _server.start();
        }
        catch (final MultiException e) {
            throw new JellyTagException(e);
        }

        // set variable to value if required
        if (getVar() != null) {
            getContext().setVariable(getVar(), _server);
        }
    }

    /**
     * Getter for property logFileName.
     *
     * @return Value of property logFileName.
     */
    public String getLogFileName() {
        return _logFileName;
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
     * Setter for property logFileName.
     *
     * @param logFileName New value of property logFileName.
     */
    public void setLogFileName(final String logFileName) {
        _logFileName = logFileName;
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
