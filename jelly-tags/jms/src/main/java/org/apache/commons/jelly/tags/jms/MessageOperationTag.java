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

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.messenger.Messenger;

/** An abstract base class for JMS Message operation tags such as send, receive or call.
  */
public abstract class MessageOperationTag extends TagSupport implements ConnectionContext {

    /** The Messenger used to access the JMS connection */
    private Messenger connection;

    /** The Destination */
    private Destination destination;

    /** The String subject used to find a destination */
    private String subject;

    public MessageOperationTag() {
    }

    /**
     * Strategy Method allowing derived classes to change this behavior
     */
    protected Messenger findConnection() throws JellyTagException, JMSException {
        final ConnectionContext messengerTag = (ConnectionContext) findAncestorWithClass( ConnectionContext.class );
        if ( messengerTag == null ) {
            throw new JellyTagException("This tag must be within a <jms:connection> tag or the 'connection' attribute should be specified");
        }
        return messengerTag.getConnection();
    }

    /**
     * Strategy Method allowing derived classes to change this behavior
     */
    protected Destination findDestination(final String subject) throws JellyTagException, JMSException {
        return getConnection().getDestination(subject);
    }

    // Properties
    //-------------------------------------------------------------------------
    @Override
    public Messenger getConnection() throws JellyTagException, JMSException {
        if ( connection == null ) {
            return findConnection();
        }
        return connection;
    }

    public Destination getDestination() throws JellyTagException, JMSException {
        // if we have a subject defined, lets use it to find the destination
        if (destination == null && subject != null) {
            destination = findDestination(subject);
        }
        return destination;
    }

    /**
     * Sets the Messenger (the JMS connection pool) that will be used to send the message
     */
    public void setConnection(final Messenger connection) {
        this.connection = connection;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the JMS Destination to be used by this tag
     */
    public void setDestination(final Destination destination) {
        this.destination = destination;
    }

    /**
     * Sets the subject as a String which is used to create the
     * JMS Destination to be used by this tag
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }
}
