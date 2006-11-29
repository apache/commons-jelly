/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
    
    /**
     * Fail if !expected.equals(actual). If expected is null, actual must be.
     * @param message failure message.
     * @param expected expected value.
     * @param actual actual value to compare against expected.
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertEquals(String message, Object expected, Object actual)
        throws JellyAssertionFailedError
    {
        if (expected == null)
        {
            assertTrue(message, actual == null);
        }
        else
        {
            assertTrue(message, expected.equals(actual));
        }
    }

    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(Object expected, Object actual)
    throws JellyAssertionFailedError
    {
        assertEquals(DEFAULT_MESSAGE, expected, actual);
    }
    
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, boolean expected, boolean actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(boolean expected, boolean actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, byte expected, byte actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(byte expected, byte actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, char expected, char actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(char expected, char actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, double expected, double actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(double expected, double actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, float expected, float actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(float expected, float actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, int expected, int actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(int expected, int actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, long expected, long actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(long expected, long actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(String message, short expected, short actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(short expected, short actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }

    /**
     * Fail if actual is not null
     * @param message failure message
     * @param actual value to check
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertNull(String message, Object actual)
    {
        assertTrue(message, actual == null);
    }
    /**
     * @see assertNull(String, Object)
     */
    protected void assertNull(Object actual)
    {
        assertNull(DEFAULT_MESSAGE, actual);
    }

    /**
     * Fail if actual is null
     * @param message failure message
     * @param actual value to check
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertNotNull(String message, Object actual)
    {
        assertTrue(message, actual != null);
    }
    /**
     * @see assertNotNull(String, Object)
     */
    protected void assertNotNull(Object actual)
    {
        assertNotNull(DEFAULT_MESSAGE, actual);
    }

    /**
     * Fail if expected != actual. If expected is null, actual must not be.
     * @param message failure message.
     * @param expected expected value.
     * @param actual actual value to compare against expected.
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertSame(String message, Object expected, Object actual)
        throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertSame(String, Object, Object)
     */
    protected void assertSame(Object expected, Object actual)
    throws JellyAssertionFailedError
    
    {
        assertSame(DEFAULT_MESSAGE, expected, actual);
    }

    /**
     * Fail if expected == actual. If expected is null, actual must be.
     * @param message failure message.
     * @param expected expected value.
     * @param actual actual value to compare against expected.
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertNotSame(String message, Object expected, Object actual)
        throws JellyAssertionFailedError
    {
        assertTrue(message, expected != actual);
    }
    /**
     * @see #assertNotSame(String, Object, Object)
     */
    protected void assertNotSame(Object expected, Object actual)
    throws JellyAssertionFailedError
    
    {
        assertNotSame(DEFAULT_MESSAGE, expected, actual);
    }
}
