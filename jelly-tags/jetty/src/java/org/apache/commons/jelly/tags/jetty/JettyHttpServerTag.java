/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/JettyHttpServerTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
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
import org.mortbay.util.LogSink;
import org.mortbay.util.OutputStreamLogSink;
import org.mortbay.util.Resource;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Declare an instance of a Jetty http server
 *
 * @author  rtl
 * @version $Id: JettyHttpServerTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 */
public class JettyHttpServerTag extends TagSupport {

    /** default port to create listeners for */
    public static final int DEFAULT_PORT = 8100;

    /** default host to create listeners/context for */
    public static final String DEFAULT_HOST = "localhost";

    /** default context to create context for */
    public static final String DEFAULT_CONTEXT_PATH = "/";

    /** default resource base to use for context */
    public static final String DEFAULT_RESOURCE_BASE = "./docRoot";

    /** default log file for Jetty */
    public static final String DEFAULT_LOG_FILE = "jetty.log";

    /** The Log to which logging calls will be made. */
    private static final org.apache.commons.logging.Log log =
        LogFactory.getLog(JettyHttpServerTag.class);

    /** the log sink for the Jety server */
    private static OutputStreamLogSink _logSink;

    // static initialisation
    {
        // setup a log for Jetty with a default filename
        try {
            _logSink = new OutputStreamLogSink(DEFAULT_LOG_FILE);
            //_logSink.start();
            Log.instance().add(_logSink);
        } catch (Exception ex ) {
            log.error(ex.getLocalizedMessage());
        }

    }

    /** unique identifier of the tag/ variable to store result in */
    private String _var;

    /** the http server for this tag */
    private HttpServer _server;

    /** filename of Jetty log file - with default */
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
     * Perform the tag functionality. In this case, create an http server after
     * making sure that it has at least one context and associated http handler,
     * creating defaults if it doesn't
     *
     * @param xmlOutput where to send output
     * @throws Exception when an error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws Exception {

        try {
            URL logFileURL = getContext().getResource(getLogFileName());
            _logSink.setFilename(logFileURL.getPath());
            _logSink.start();
        } catch (Exception ex ) {
            log.error(ex.getLocalizedMessage());
        }

        // allow nested tags first, e.g body
        invokeBody(xmlOutput);

        // if no listeners create a default port listener
        if (_server.getListeners().length == 0) {
            SocketListener listener=new SocketListener();
            listener.setPort(DEFAULT_PORT);
            listener.setHost(DEFAULT_HOST);
            _server.addListener(listener);
        }

        // if no context/s create a default context
        if (_server.getContexts().length == 0) {
            log.info("Creating a default context");
            // Create a context
            HttpContext context = _server.getContext(DEFAULT_HOST,
                                                    DEFAULT_CONTEXT_PATH);

            // Serve static content from the context
            URL baseResourceURL = getContext().getResource(DEFAULT_RESOURCE_BASE);
            Resource resource = Resource.newResource(baseResourceURL);
            context.setBaseResource(resource);
            _server.addContext(context);
        }

        // check that all the contexts have at least one handler
        // if not then add a default resource handler and a not found handler
        HttpContext[] allContexts = _server.getContexts();
        for (int i = 0; i < allContexts.length; i++) {
            HttpContext currContext = allContexts[i];
            if (currContext.getHandlers().length == 0) {
                log.info("Adding resource and not found handlers to context:" +
                         currContext.getContextPath());
                currContext.addHandler(new ResourceHandler());
                currContext.addHandler(new NotFoundHandler());
            }
        }

        // Start the http server
        _server.start();

        // set variable to value if required
        if (getVar() != null) {
            getContext().setVariable(getVar(), _server);
        }
    }

    /**
     * Add an http listener to the server instance
     *
     * @param listener the listener to add
     */
    public void addListener(HttpListener listener) {
        _server.addListener(listener);
    }

    /**
     * Add an http context to the server instance
     *
     * @param context the context to add
     */
    public void addContext(HttpContext context) {
        _server.addContext(context);
    }

    /* ------------------------------------------------------------ */
    /**
     * Add a user authentication realm to the server instance
     *
     * @param realm the realm to add
     * @return the realm added
     */
    public UserRealm addRealm(UserRealm realm)
    {
        return _server.addRealm(realm);
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property var.
     *
     * @return Value of property var.
     */
    public String getVar() {
        return _var;
    }

    /**
     * Setter for property var.
     *
     * @param var New value of property var.
     */
    public void setVar(String var) {
        _var = var;
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
     * Setter for property logFileName.
     *
     * @param logFileName New value of property logFileName.
     */
    public void setLogFileName(String logFileName) {
        _logFileName = logFileName;
    }

}
