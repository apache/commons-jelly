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

/**
 * A class that holds proxy details for a session.
 * At the moment this is a placeholder for two simple properties that may
 * get added to as time goes by
 */
public class Proxy {

    /** The port number that represents port is unassigned */
    public static final int PORT_UNSPECIFIED = -1;
    /** The host to use as a proxy */
    private String _host;
    /** The port to send proxied requests on */
    private int _port;

    /**
     * Creates a new instance of Proxy
     */
    public Proxy() {
        this(null, Proxy.PORT_UNSPECIFIED);
    }

    /**
     * Create a proxy given a host name and port number.
     *
     * @param host the host name of the proxy to be used.
     * @param port the port to send proxied requests on.
     */
    public Proxy(final String host, final int port) {
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
     * Getter for property port.
     *
     * @return the port to send proxied requests on.
     */
    public int getPort() {
        return _port;
    }

    /**
     * Setter for property host.
     *
     * @param host the host name of the proxy to be used.
     */
    public void setHost(final String host) {
        _host = host;
    }

    /**
     * Setter for property port.
     *
     * @param port the port to send proxied requests on.
     */
    public void setPort(final int port) {
        _port = port;
    }

}
