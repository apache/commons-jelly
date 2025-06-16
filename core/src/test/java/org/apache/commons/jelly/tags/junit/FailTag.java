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
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * This tag causes a failure message. The message can either
 * be specified in the tags body or via the message attribute.
 */
public class FailTag extends AssertTagSupport {

    private String message;

    public FailTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        String message = getMessage();
        if ( message == null ) {
            message = getBodyText();
        }
        fail( message );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the failure message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the failure message. If this attribute is not specified then the
     * body of this tag will be used instead.
     */
    public void setMessage(final String message) {
        this.message = message;
    }
}
