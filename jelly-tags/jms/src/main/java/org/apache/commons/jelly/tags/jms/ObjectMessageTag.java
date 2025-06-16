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

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.jelly.JellyTagException;

/** Creates a JMS ObjectMessage
  */
public class ObjectMessageTag extends MessageTag {

    private Serializable object;

    public ObjectMessageTag() {
    }

    // Properties
    //-------------------------------------------------------------------------

    // Implementation methods
    //-------------------------------------------------------------------------
    @Override
    protected Message createMessage() throws JellyTagException {
        final Serializable value = object != null ? object : getBodyText();
        try {
            return getConnection().createObjectMessage(value);
        }
        catch (final JMSException e) {
            throw new JellyTagException(e);
        }
    }

    /**
     * Sets the body of the message, a serializable java object.
     * If this value is not set or the value is null then the content
     * of the tag will be used instead.
     */
    public void setObject(final Serializable object) {
        this.object = object;
    }
}
