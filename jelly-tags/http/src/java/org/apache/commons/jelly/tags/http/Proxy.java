/*
 *
 *
 *
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

/**
 * A class that holds proxy details for a session. 
 * At the moment this is a placeholder for two simple properties that may
 * get added to as time goes by
 *
 * @author  dion
 * @version $Id: Proxy.java,v 1.3 2002/07/04 14:06:32 dion Exp $
 */
public class Proxy {
    
    /** the host to use as a proxy */
    private String _host;
    /** the port to send proxied requests on */
    private int _port;
    /** the port number that represents port is unassigned */
    public static final int PORT_UNSPECIFIED = -1;
    
    /**
     * Creates a new instance of Proxy
     */
    public Proxy() {
        this(null, Proxy.PORT_UNSPECIFIED);
    }
    
    /** 
     * Create a proxy given a host name and port number .
     *
     * @param host the host name of the proxy to be used.
     * @param port the port to send proxied requests on.
     */
    public Proxy(String host, int port) {
        setHost(host);
        setPort(port);
    }
    
    /**
     * Getter for property host.
     *
     * @return the host name of the proxy to be used.
     */
    public String getHost() {
        return _host;
    }
    
    /**
     * Setter for property host.
     *
     * @param host the host name of the proxy to be used.
     */
    public void setHost(String host) {
        _host = host;
    }
    
    /**
     * Getter for property port.
     *
     * @return the port to send proxied requests on.
     */
    public int getPort() {
        return _port;
    }
    
    /**
     * Setter for property port.
     *
     * @param port the port to send proxied requests on.
     */
    public void setPort(int port) {
        _port = port;
    }
    
}
