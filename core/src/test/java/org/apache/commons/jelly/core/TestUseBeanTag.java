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
package org.apache.commons.jelly.core;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.test.BaseJellyTest;

import junit.framework.TestSuite;

/**
 * Tests for UseBean tag
 */
public class TestUseBeanTag extends BaseJellyTest {

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestUseBeanTag.class);
    }

    public TestUseBeanTag(final String name) {
        super(name);
    }

    /** Test set a bad property name on a bean, should fail.
     * @throws Exception
     */
    public void testBadProperty() throws Exception {
        setUpScript("testUseBeanTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.badProperty",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        final Exception e = (Exception)getJellyContext().getVariable("ex");
        assertNotNull("Should have failed to set invalid bean property", e);
    }

    /**
     * test extension
     */
    public void testExtension() throws Exception {
        setUpScript("testUseBeanTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.extension",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNull("name set wrongly", customer.getName());
        assertEquals("city not set", "sydney", customer.getCity());
    }

    /** Test set a bad property name on a bean, this should be silently ignored.
     * @throws Exception
     */
    public void testIgnoredBadProperty() throws Exception {
        setUpScript("testUseBeanTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.badPropertyIgnored",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNotNull("Should have ignored invalid bean property", customer);
    }

    /**
     * Test a simple useBean tag works ok
     * @throws Exception
     */
    public void testSimple() throws Exception{
        setUpScript("testUseBeanTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.simple",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertEquals("name not set", "testing", customer.getName());
        assertEquals("city not set", "sydney", customer.getCity());
    }
}
