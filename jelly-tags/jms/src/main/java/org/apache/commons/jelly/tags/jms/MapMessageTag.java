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

import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.jelly.JellyTagException;

/** Creates a JMS MapMessage
  */
public class MapMessageTag extends MessageTag {

    public MapMessageTag() {
    }

    public void addEntry(final String name, final Object value) throws JellyTagException {
        final MapMessage message = (MapMessage) getMessage();
        try {
            message.setObject(name, value);
        }
        catch (final JMSException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the Map of entries to be used for this Map Message
     */
    public void setMap(final Map map) throws JellyTagException {
        final MapMessage message = (MapMessage) getMessage();
        for (final Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) iter.next();
            final String name = entry.getKey().toString();
            final Object value = entry.getValue();

            try {
                message.setObject(name, value);
            }
            catch (final JMSException e) {
                throw new JellyTagException(e);
            }
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    @Override
    protected Message createMessage() throws JellyTagException {
        try {
            return getConnection().createMapMessage();
        } catch (final JMSException e) {
            throw new JellyTagException(e);
        }
    }
}
