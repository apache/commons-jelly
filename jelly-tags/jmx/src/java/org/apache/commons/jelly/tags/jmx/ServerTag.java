/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties..
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class ServerTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ServerTag.class);

    private MBeanServer server;

    public ServerTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {

        // force the creation of a Server
        MBeanServer server = getServer();

        // allow children to register beans
        invokeBody(output);
    }


    // Properties
    //-------------------------------------------------------------------------
    /**
     * @return MBeanServer
     */
    public MBeanServer getServer() {
        if (server == null) {
            server = createServer();
        }
        return server;
    }

    /**
     * Sets the server.
     * @param server The server to set
     */
    public void setServer(MBeanServer server) {
        this.server = server;
    }



    // Implementstion methods
    //-------------------------------------------------------------------------
    /**
     * Factory method to lazily create an MBeanServer if none is supplied
     *
     * @return MBeanServer
     */
    protected MBeanServer createServer() {
        return MBeanServerFactory.newMBeanServer();
    }

}
