/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
 * useful for implementation inheritence.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public abstract class AssertTagSupport extends XPathTagSupport {

    /** the default message to display if none is given */
    private static String DEFAULT_MESSAGE = "assertion failed";
    
    public AssertTagSupport() {
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Produces a failure assertion with the given message
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void fail(String message) throws JellyAssertionFailedError {
        throw new JellyAssertionFailedError(message);
    }

    /**
     * Produces a failure assertion with a default message
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void fail() throws JellyAssertionFailedError {
        throw new JellyAssertionFailedError(DEFAULT_MESSAGE);
    }

    /**
     * Produces a failure assertion with the given message and added detail.
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void fail(String message, String detail) throws JellyAssertionFailedError {
        if (message == null || message.length() == 0) {
            fail(detail);
        }
        else {
            fail(message + ". Assertion failed while " + detail);
        }
    }

    /**
     * Produces a failure if the actual value was not equal to the expected value
     * @throws JellyAssertionFailedError if expected != actual.
     */
    protected void failNotEquals(String message, Object expected, Object actual, String expressions) throws JellyAssertionFailedError {
        String formatted= "";
        if (message != null) {
            formatted = message +" ";
        }
        fail(formatted + "expected:[" + expected + "] but was:[" + actual + "]" + expressions);
    }
    
    /**
     * Fail if actual is not true
     * @param message failure message
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertTrue(String message, boolean actual) throws JellyAssertionFailedError
    {
        if (!actual) fail(message);
    }
    
    /**
     * Fail if actual is not true
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertTrue(boolean actual) throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, actual);
    }

    /**
     * Fail if actual is true
     * @param message failure message
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertFalse(String message, boolean actual) throws JellyAssertionFailedError
    {
        if (actual) fail(message);
    }
    
    /**
     * Fail if actual is true
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertFalse(boolean actual) throws JellyAssertionFailedError
    {
        assertFalse(DEFAULT_MESSAGE, actual);
    }
}
