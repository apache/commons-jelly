/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/JellyResourceHttpHandler.java,v 1.3 2002/07/14 12:38:22 dion Exp $
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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.StringBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * The actual http handler implementation for an http context in an http server
 *
 * @author  rtl
 * @version $Id: JellyResourceHttpHandler.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 */
class JellyResourceHttpHandler extends AbstractHttpHandler {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JellyResourceHttpHandler.class);

    /** The name of the var to check if setHandled should not be set to true . */
    private static final String OVERRIDE_SET_HANDLED_VAR = "overrideSetHandled";

    /** The list of  tags registered to handle a request method  */
    private Map _tagMap;

    /** The place where to output the results of the tag body */
    private XMLOutput _xmlOutput;

    /** Creates a new instance of JellyResourceHttpHandler */
    public JellyResourceHttpHandler( XMLOutput xmlOutput ) {
        _tagMap = new HashMap();
        _xmlOutput = xmlOutput;
    }

    /*
     * register this tag as the handler for the specified method
     *
     * @param tag the tag to be registered
     * @param method the name of the http method which this tag processes
     */
    public void registerTag(Tag tag, String method){
        _tagMap.put(method.toLowerCase(), tag);
    }

    /*
     * handle an http request
     *
     * @param pathInContext the path of the http request
     * @param pathParams the parameters (if any) of the http request
     * @param request the actual http request
     * @param response the place for any response
     *
     * @throws HttpException when an error occurs
     * @throws IOException when an error occurs
     */
    public void handle(String pathInContext,
                       String pathParams,
                       HttpRequest request,
                       HttpResponse response)
        throws HttpException, IOException
    {
        Tag handlerTag = (Tag) _tagMap.get(request.getMethod().toLowerCase());
        if (null != handlerTag) {
            // setup the parameters in the jelly context
            JellyContext jellyContext = handlerTag.getContext();
            jellyContext.setVariable( "pathInContext", pathInContext);
            jellyContext.setVariable( "pathParams", pathParams);
            jellyContext.setVariable( "request", request);
            jellyContext.setVariable( "requestBody", getRequestBody(request));
            jellyContext.setVariable( "response", response);

            try {
                handlerTag.invokeBody(_xmlOutput);
                // only call set handled if tag has not requested an override
                // if it has requested an override then reset the request
                if (null == jellyContext.getVariable(OVERRIDE_SET_HANDLED_VAR)) {
                    request.setHandled(true);
                    response.commit();
                } else {
                    jellyContext.removeVariable(OVERRIDE_SET_HANDLED_VAR);
                }
            } catch (Exception ex ) {
                throw new HttpException(HttpResponse.__500_Internal_Server_Error,
                                        "Error invoking method handler tag: " + ex.getLocalizedMessage());
            }
        } else {
            log.info("No handler for request:" +
                      request.getMethod() + " path:" +
                      response.getHttpContext().getContextPath() +
                      pathInContext);
        }

        return;
    }

    public String getRequestBody(HttpRequest request) throws IOException {

        // read the body as a string from the input stream
        InputStream is = request.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        char[] buffer = new char[1024];
        int len;

        while ((len = isr.read(buffer, 0, 1024)) != -1)
          sb.append(buffer, 0, len);

        if (sb.length() > 0)
          return sb.toString();
        else
          return null;

    }
}

