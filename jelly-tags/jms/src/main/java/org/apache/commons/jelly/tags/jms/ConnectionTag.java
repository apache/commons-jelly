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
package org.apache.commons.jelly.tags.jms;

import javax.jms.JMSException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;

/** Defines a JMS connection for use by other JMS tags.
  */
public class ConnectionTag extends TagSupport implements ConnectionContext {

    /** The variable name to create */
    private String var;

    /** Stores the name of the map entry */
    private String name;

    /** The Messenger */
    private Messenger connection;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        try {
            connection = MessengerManager.get( name );
        }
        catch (final JMSException e) {
            throw new JellyTagException(e);
        }

        if (connection == null) {
            throw new JellyTagException( "Could not find a JMS connection called: " + name );
        }

        if ( var != null ) {
            context.setVariable( var, connection );
        }

        invokeBody(output);
    }

    // ConnectionContext interface
    //-------------------------------------------------------------------------
    @Override
    public Messenger getConnection() {
        return connection;
    }

    // Properties
    //-------------------------------------------------------------------------

    /** Sets the name of the Messenger (JMS connection pool) to use
      */
    public void setName(final String name) {
        this.name = name;
    }

    /** Sets the variable name to use for the exported Messenger (JMS connection pool)
      */
    public void setVar(final String var) {
        this.var = var;
    }
}
