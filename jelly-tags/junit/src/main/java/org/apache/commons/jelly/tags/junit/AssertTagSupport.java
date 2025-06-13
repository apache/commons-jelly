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

    /** The default message to display if none is given */
    private static String DEFAULT_MESSAGE = "assertion failed";

    public AssertTagSupport() {
    }

    /**
     * Produces a failure assertion with the given message
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void fail(final String message) throws JellyAssertionFailedError {
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
     * @throws JellyAssertionFailedError if expected != actual.
     */
    protected void failNotEquals(final String message, final Object expected, final Object actual, final String expressions) throws JellyAssertionFailedError {
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
    protected void assertTrue(final String message, final boolean actual) throws JellyAssertionFailedError
    {
        if (!actual) {
            fail(message);
        }
    }

    /**
     * Fail if actual is not true
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertTrue(final boolean actual) throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, actual);
    }

    /**
     * Fail if actual is true
     * @param message failure message
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertFalse(final String message, final boolean actual) throws JellyAssertionFailedError
    {
        if (actual) {
            fail(message);
        }
    }

    /**
     * Fail if actual is true
     * @param actual value to test
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertFalse(final boolean actual) throws JellyAssertionFailedError
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
    protected void assertEquals(final String message, final Object expected, final Object actual)
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
    protected void assertEquals(final Object expected, final Object actual)
    throws JellyAssertionFailedError
    {
        assertEquals(DEFAULT_MESSAGE, expected, actual);
    }

    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final boolean expected, final boolean actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final boolean expected, final boolean actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }

    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final byte expected, final byte actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final byte expected, final byte actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final char expected, final char actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final char expected, final char actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final double expected, final double actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final double expected, final double actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final float expected, final float actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final float expected, final float actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final int expected, final int actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final int expected, final int actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final long expected, final long actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final long expected, final long actual)
    throws JellyAssertionFailedError
    {
        assertTrue(DEFAULT_MESSAGE, expected == actual);
    }
    /**
     * @see #assertEquals(String, Object, Object)
     */
    protected void assertEquals(final String message, final short expected, final short actual)
    throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertEquals(Object, Object)
     */
    protected void assertEquals(final short expected, final short actual)
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
    protected void assertNull(final String message, final Object actual)
    {
        assertTrue(message, actual == null);
    }
    /**
     * @see #assertNull(String, Object)
     */
    protected void assertNull(final Object actual)
    {
        assertNull(DEFAULT_MESSAGE, actual);
    }

    /**
     * Fail if actual is null
     * @param message failure message
     * @param actual value to check
     * @throws JellyAssertionFailedError to signify failure
     */
    protected void assertNotNull(final String message, final Object actual)
    {
        assertTrue(message, actual != null);
    }
    /**
     * @see #assertNotNull(String, Object)
     */
    protected void assertNotNull(final Object actual)
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
    protected void assertSame(final String message, final Object expected, final Object actual)
        throws JellyAssertionFailedError
    {
        assertTrue(message, expected == actual);
    }
    /**
     * @see #assertSame(String, Object, Object)
     */
    protected void assertSame(final Object expected, final Object actual)
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
    protected void assertNotSame(final String message, final Object expected, final Object actual)
        throws JellyAssertionFailedError
    {
        assertTrue(message, expected != actual);
    }
    /**
     * @see #assertNotSame(String, Object, Object)
     */
    protected void assertNotSame(final Object expected, final Object actual)
    throws JellyAssertionFailedError

    {
        assertNotSame(DEFAULT_MESSAGE, expected, actual);
    }
}
