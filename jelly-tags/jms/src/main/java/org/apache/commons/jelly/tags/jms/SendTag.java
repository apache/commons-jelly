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
import javax.jms.Message;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/** Sends a JMS message to some destination.
  */
public class SendTag extends MessageOperationTag {

    /** The JMS Message to be sent */
    private Message message;

    public SendTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // evaluate body as it may contain a <destination> or message tag
        invokeBody(output);

        final Message message = getMessage();
        if ( message == null ) {
            throw new JellyTagException( "No message specified. Either specify a 'message' attribute or use a nested <jms:message> tag" );
        }

        try {
            final Destination destination = getDestination();
            if ( destination == null ) {
                throw new JellyTagException( "No destination specified. Either specify a 'destination' attribute or use a nested <jms:destination> tag" );
            }
            getConnection().send( destination, message );
        }
        catch (final JMSException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the JMS message to be sent
     */
    public void setMessage(final Message message) {
        this.message = message;
    }
}
