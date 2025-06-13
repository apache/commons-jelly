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

import javax.jms.MessageListener;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.messenger.tool.StopWatchMessageListener;

/**
 * This tag can be used to measure the amount of time it takes to process JMS messages.
 * This tag can be wrapped around any custom JMS tag which consumes JMS messages.
 */
public class StopwatchTag extends MessageOperationTag implements ConsumerTag {

    /** The underlying MessageListener */
    private MessageListener messageListener;

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog( StopwatchTag.class );

    /** The message group size */
    private int groupSize = 1000;

    public StopwatchTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        // evaluate body as it may contain child tags to register a MessageListener
        invokeBody(output);

        final MessageListener listener = getMessageListener();

        final ConsumerTag tag = (ConsumerTag) findAncestorWithClass(ConsumerTag.class);
        if (tag == null) {
            throw new JellyTagException("This tag must be nested within a ConsumerTag like the subscribe tag");
        }

        // clear the listener for the next tag invocation, if caching is employed
        setMessageListener(null);

        final StopWatchMessageListener stopWatch = new StopWatchMessageListener(listener);
        stopWatch.setGroupSize(groupSize);
        stopWatch.setLog(log);

        // perform the consumption
        tag.setMessageListener(stopWatch);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the number of messages in the group before the performance statistics are logged
     */
    public int getGroupSize() {
        return groupSize;
    }

    /**
     * @return the logger to which statistic messages will be sent
     */
    public Log getLog() {
        return log;
    }

    /**
     * @return the MessageListener which this listener delegates to
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    /**
     * Sets the number of messages in the group before the performance statistics are logged
     */
    public void setGroupSize(final int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Sets the logger to which statistic messages will be sent
     */
    public void setLog(final Log log) {
        this.log = log;
    }

    /**
     * Sets the JMS messageListener used to consume JMS messages on the given destination
     */
    @Override
    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

}
