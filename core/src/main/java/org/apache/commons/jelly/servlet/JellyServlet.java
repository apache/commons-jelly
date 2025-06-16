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

package org.apache.commons.jelly.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Servlet for handling display of Jelly-fied XML files. Modeled after VelocityServlet.
 */
public class JellyServlet extends HttpServlet {
    /**
     * The HTTP request object context key.
     */
    public static final String REQUEST = "request";

    /**
     * The HTTP response object context key.
     */
    public static final String RESPONSE = "response";

    /**
     * See org.apache.velocity.servlet.VelocityServlet#createContext
     * @param req
     * @param res
     * @return a new context.
     */
    protected JellyContext createContext(
        final HttpServletRequest req,
        final HttpServletResponse res) {

        final JellyContext ctx = new JellyServletContext(getServletContext());
        ctx.setVariable(REQUEST, req);
        ctx.setVariable(RESPONSE, res);
        return ctx;
    }

    @Override
    protected void doGet(
        final HttpServletRequest request,
        final HttpServletResponse response)
        throws ServletException, IOException {

        doRequest(request, response);
    }

    @Override
    protected void doPost(
        final HttpServletRequest request,
        final HttpServletResponse response)
        throws ServletException, IOException {

        doRequest(request, response);
    }

    /**
     * Handles all requests
     * @param req HttpServletRequest object containing client request
     * @param res HttpServletResponse object for the response
     * @throws ServletException
     * @throws IOException
     */
    protected void doRequest(final HttpServletRequest req, final HttpServletResponse res)
        throws ServletException, IOException {

        final JellyContext context = createContext(req, res);
        try {
            final URL script = getScript(req);
            runScript(script, context, req, res);
        }
        catch (final Exception e) {
            error(req, res, e);
        }
    }

    /**
     * Invoked when there is an error thrown in any part of doRequest() processing.
     * <br><br>
     * Default will send a simple HTML response indicating there was a problem.
     *<br><br>
     * Ripped from VelocityServlet.
     *
     * @param request original HttpServletRequest from servlet container.
     * @param response HttpServletResponse object from servlet container.
     * @param cause  Exception that was thrown by some other part of process.
     */
    protected void error(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Exception cause)
        throws ServletException, IOException {

        final StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<title>Error</title>");
        html.append("<body bgcolor=\"#ffffff\">");
        html.append("<h2>JellyServlet : Error processing the script</h2>");
        html.append("<pre>");
        final String why = cause.getMessage();
        if (why != null && why.trim().length() > 0) {
            html.append(why);
            html.append("<br>");
        }

        final StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));

        html.append(sw.toString());
        html.append("</pre>");
        html.append("</body>");
        html.append("</html>");
        response.getOutputStream().print(html.toString());
    }

    /**
     * <p>
     * Either use the query parameter "script", or the URI itself
     * to denote the script to run.
     * </p>
     * <p>
     * Example: script=index.jelly or http://localhost:8080/foo/index.jelly.
     * </p>
     *
     * See org.apache.velocity.servlet.VelocityServlet#getTemplate
     * @param req
     * @return a URL.
     * @throws MalformedURLException
     */
    protected URL getScript(final HttpServletRequest req)
        throws MalformedURLException {

        String scriptUrl = req.getParameter("script");
        if (scriptUrl == null) {
            scriptUrl = req.getPathInfo();
        }
        final URL url = getServletContext().getResource(scriptUrl);
        if (url == null) {
            throw new IllegalArgumentException("Invalid script url:" + scriptUrl);
        }
        return url;
    }

    /**
     * See org.apache.velocity.servlet.VelocityServlet#mergeTemplate
     * @param script
     * @param context
     * @param req
     * @param res
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws JellyException
     */
    protected void runScript(
        final URL script,
        final JellyContext context,
        final HttpServletRequest req,
        final HttpServletResponse res)
        throws IOException, UnsupportedEncodingException, JellyException {

        final ServletOutputStream output = res.getOutputStream();
        final XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
        context.runScript(script, xmlOutput);
        xmlOutput.flush();
        xmlOutput.close();
        output.flush();
    }
}
