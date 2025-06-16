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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This tag creates a JMS MessageListener which will invoke this
 * tag's body whenever a JMS Message is received. The JMS Message
 * will be available via a variable, which defaults to the 'message'
 * variable name, but can be overloaded by the var attribute.
 */
public class OnMessageTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(OnMessageTag.class);

    private String var = "message";

    public OnMessageTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final ConsumerTag tag = (ConsumerTag) findAncestorWithClass(ConsumerTag.class);
        if (tag == null) {
            throw new JellyTagException("This tag must be nested within a ConsumerTag like the subscribe tag");
        }

        final JellyContext childContext = context.newJellyContext();
        final Script script = getBody();
        final XMLOutput childOutput = output;

        final MessageListener listener = message -> {
            childContext.setVariable(var, message);
            try {
                script.run(childContext, childOutput);
            }
            catch (final Exception e) {
                log.error("Caught exception processing message: " + message + ". Exception: " + e, e);
            }
        };

        // perform the consumption
        tag.setMessageListener(listener);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the variable used to make the JMS message available to this tags
     * body when a message is received.
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
