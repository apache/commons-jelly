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
import org.mortbay.http.HashUserRealm;

/**
 * Declare a user realm for a Jetty http server
 */
public class RealmTag extends TagSupport {

    /** Parameter name with default*/
    private String _name;

    /** Parameter config, with default */
    private String _config;

    /** Creates a new instance of RealmTag */
    public RealmTag() {
    }

    /**
     * Perform the tag functionality. In this case, add a realm with the
     * specified name using the specified config (properties) file to the
     * parent server,
     *
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final JettyHttpServerTag httpserver = (JettyHttpServerTag) findAncestorWithClass(
            JettyHttpServerTag.class);
        if ( httpserver == null ) {
            throw new JellyTagException( "<realm> tag must be enclosed inside a <server> tag" );
        }
        if (null == getName() || null == getConfig()) {
            throw new JellyTagException( "<realm> tag must have a name and a config" );
        }

        // convert the config string to a URL
        // (this makes URL's relative to the location of the script
        try {
            final URL configURL = getContext().getResource(getConfig());
            httpserver.addRealm( new HashUserRealm(getName(), configURL.toString() ) );
        } catch (final IOException e) {
            throw new JellyTagException(e);
        }

        invokeBody(xmlOutput);
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property config.
     *
     * @return value of property config.
     */
    public String getConfig() {
        return _config;
    }

    /**
     * Getter for property name.
     *
     * @return value of property name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Setter for property config.
     *
     * @param config New value of property config.
     */
    public void setConfig(final String config) {
        _config = config;
    }

    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     */
    public void setName(final String name) {
        _name = name;
    }

}
