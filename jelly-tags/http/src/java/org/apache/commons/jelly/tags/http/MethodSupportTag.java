/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/taglibs/beanshell/src/java/org/apache/commons/jelly/tags/beanshell/BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/21 07:58:55 $
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
 * $Id: BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.http;

import java.net.MalformedURLException;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;

/** 
 * Abstract base class for a tag which invokes a HTTP method
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public abstract class MethodSupportTag extends TagSupport {

    /** The variable name to create the HttpMethod object */
    private String var;
        
    /** Sets the URL of this request */
    private String url;
    
    /** The HTTP client used to invoke methods */
    private HttpConnection connection;        
    
    /** the HTTP connection factory used create HTTP connections */
    private HttpConnectionManager connectionManager;
                
    /** The state for this http request, cookies etc. */
    private HttpState state;
    
    public void addHeader(String name, String value) throws HttpException, MalformedURLException {
        getMethod().addRequestHeader(name, value);
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {        
        // ### should probably use HttpClient instead
/*
        HttpClient client = getHttpClient();
        client.startSession( getUrl() );
        
        try {
            // must use inner tags to extract the output....
            invokeBody(output)
        }
        finally {
            client.endSession();
        }
        
*/        
         
        HttpConnection connection = getConnection();
        if ( connection == null ) {
            throw new JellyException( 
                "No HTTP connection available. "
                + "This tag should have a 'uri' attribute or be nested inside "
                + "a <http:connection> tag" 
            );
        }

        HttpMethod method = getMethod();
        if ( method == null ) {
            throw new JellyException( "No HTTP Method available for this tag to execute" );
        }
        int result = method.execute(getState(), connection);

        if ( var != null ) {
            context.setVariable(var, method);
        }
    }
    
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the HTTP method to invoke
     */
    public abstract HttpMethod getMethod() throws HttpException, MalformedURLException;

    /**
     * @return the state of this HTTP request, the cookies used etc
     */
    public HttpState getState() {
        if ( state == null ) {
            state = new HttpState();
        }
        return state;
    }
        
        
    /**
     * @return the HTTP connection, creating one if required
     */
    public HttpConnection getConnection() throws HttpException, MalformedURLException {
        if ( connection == null ) {
            connection = createConnection();
        }
        return connection;
    }
            
    /**
     * Sets the HTTP connection used to perform the HTTP operation
     * 
     * @jelly:optional
     */
    public void setConnection(HttpConnection connection) {
        this.connection = connection;
    }    
    
    /**
     * @return the HTTP connection factory, creating one if required
     */
    public HttpConnectionManager getConnectionManager() {
        if ( connectionManager == null ) {
            connectionManager = new HttpConnectionManager();
        }
        return connectionManager;
    }
       
    /**
     * Sets the HTTP connection factory used to create HTTP connections
     * 
     * @jelly:optional
     */
    public void setConnectionManager(HttpConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }    
    
    /** Sets the variable name for the HttpMethod object 
     * 
     * @jelly:optional
      */
    public void setVar(String var) {
        this.var = var;
    }


    /**
     * @return the URL of this HTTP request
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets the URL of this request
     * 
     * @jelly:required
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * A Factory method to create a new HTTP connection
     */
    protected HttpConnection createConnection() throws HttpException, MalformedURLException {
        return getConnectionManager().getConnection( getUrl() );                
    }
}
