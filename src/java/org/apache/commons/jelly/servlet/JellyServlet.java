/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/servlet/JellyServlet.java,v 1.3 2002/12/17 08:32:56 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/12/17 08:32:56 $
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
 * $Id: JellyServlet.java,v 1.3 2002/12/17 08:32:56 jstrachan Exp $
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
import org.apache.commons.jelly.XMLOutput;

/**
 * Servlet for handling display of Jelly-fied XML files. Modelled after VelocityServlet.
 * 
 * @author Kelvin Tan
 * @version $Revision: 1.3 $
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

    protected void doGet(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		doRequest(request, response);
	}

	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
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
	protected void doRequest(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		JellyContext context = createContext(req, res);
		try {
            URL script = getScript(req);
			runScript(script, context, req, res);
		}
		catch (Exception e) {
			error(req, res, e);
		}
	}

	/**
	 * @see org.apache.velocity.servlet.VelocityServlet#createContext
	 * @param req
	 * @param res
	 * @return
	 */
	protected JellyContext createContext(
		HttpServletRequest req,
		HttpServletResponse res) {

		JellyContext ctx = new JellyServletContext(getServletContext());
		ctx.setVariable(REQUEST, req);
		ctx.setVariable(RESPONSE, res);
		return ctx;
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
	 * @see org.apache.velocity.servlet.VelocityServlet#getTemplate
	 * @param req
	 * @return
	 * @throws MalformedURLException
	 */
	protected URL getScript(HttpServletRequest req)
		throws MalformedURLException {

		String scriptUrl = req.getParameter("script");
		if (scriptUrl == null) {
			scriptUrl = req.getPathInfo();
		}
		URL url = getServletContext().getResource(scriptUrl);
        if (url == null) {
            throw new IllegalArgumentException("Invalid script url:" + scriptUrl);
        }
        return url;
	}

	/**
	 * @see org.apache.velocity.servlet.VelocityServlet#mergeTemplate
	 * @param script
	 * @param context
	 * @param req
	 * @param res
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	protected void runScript(
		URL script,
		JellyContext context,
		HttpServletRequest req,
		HttpServletResponse res)
		throws IOException, UnsupportedEncodingException, Exception {

		ServletOutputStream output = res.getOutputStream();
		XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
		context.runScript(script, xmlOutput);
		xmlOutput.flush();
		xmlOutput.close();
		output.flush();
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
		HttpServletRequest request,
		HttpServletResponse response,
		Exception cause)
		throws ServletException, IOException {

		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<title>Error</title>");
		html.append("<body bgcolor=\"#ffffff\">");
		html.append("<h2>JellyServlet : Error processing the script</h2>");
		html.append("<pre>");
		String why = cause.getMessage();
		if (why != null && why.trim().length() > 0) {
			html.append(why);
			html.append("<br>");
		}

		StringWriter sw = new StringWriter();
		cause.printStackTrace(new PrintWriter(sw));

		html.append(sw.toString());
		html.append("</pre>");
		html.append("</body>");
		html.append("</html>");
		response.getOutputStream().print(html.toString());
	}
}
