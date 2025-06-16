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

import org.apache.commons.jelly.xpath.XPathTagSupport;

/**
 * The abstract base class of any assertion tag which is
 * useful for implementation inheritance.
 */
public abstract class AssertTagSupport extends XPathTagSupport {

    public AssertTagSupport() {
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Produces a failure assertion with the given message
     */
    protected void fail(final String message) throws JellyAssertionFailedError {
        throw new JellyAssertionFailedError(message);
    }

    /**
     * Produces a failure assertion with the given message and added detail.
     */
    protected void fail(final String message, final String detail) throws JellyAssertionFailedError {
        if (message == null || message.length() == 0) {
            fail(detail);
        }
        else {
            fail(message + ". Assertion failed while " + detail);
        }
    }

    /**
     * Produces a failure if the actual value was not equal to the expected value
     */
    protected void failNotEquals(final String message, final Object expected, final Object actual, final String expressions) throws JellyAssertionFailedError {
        String formatted= "";
        if (message != null) {
            formatted = message +" ";
        }
        fail(formatted + "expected:[" + expected + "] but was:[" + actual + "]" + expressions);
    }

}
