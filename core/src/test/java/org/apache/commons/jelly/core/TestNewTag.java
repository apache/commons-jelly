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

import java.util.Date;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.test.BaseJellyTest;

import junit.framework.TestSuite;

/*
 */
public class TestNewTag extends BaseJellyTest {

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestNewTag.class);
    }

    public TestNewTag(final String name) {
        super(name);
    }

    public void testNewThenOverwrite() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newThenOverwrite",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Date);
    }

    public void testNewWithExpressionArg() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithExpressionArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
    }

    public void testNewWithLiteralArg() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithLiteralArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
    }

    public void testNewWithNewArg() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithNewArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        {
            assertNotNull(getJellyContext().getVariable("foo"));
            assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
            final Customer customer = (Customer)getJellyContext().getVariable("foo");
            assertNotNull(customer.getName());
            assertEquals("",customer.getName());
        }
        {
            assertNotNull(getJellyContext().getVariable("bar"));
            assertTrue(getJellyContext().getVariable("bar") instanceof Customer);
            final Customer customer = (Customer)getJellyContext().getVariable("bar");
            assertEquals("Jane Doe",customer.getName());
            assertEquals("Chicago",customer.getCity());
            assertNotNull(customer.getOrders());
            assertEquals(1,customer.getOrders().size());
            assertNotNull(customer.getOrders().get(0));
        }
        {
            assertNotNull(getJellyContext().getVariable("qux"));
            assertTrue(getJellyContext().getVariable("qux") instanceof Customer);
            final Customer customer = (Customer)getJellyContext().getVariable("qux");
            assertEquals("Jane Doe",customer.getName());
            assertEquals("Chicago",customer.getCity());
            assertNotNull(customer.getOrders());
            assertEquals(1,customer.getOrders().size());
            assertNotNull(customer.getOrders().get(0));
        }
    }

    public void testNewWithNullArg() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithNullArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNull(customer.getName());
    }

    public void testNewWithTwoArgs() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithTwoArgs",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
        assertNotNull(customer.getCity());
        assertEquals("Chicago",customer.getCity());
    }

    public void testNewWithUseBeanArg() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithUseBeanArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertEquals("Jane Doe",customer.getName());
        assertEquals("Chicago",customer.getCity());
        assertEquals("Location",customer.getLocation());
    }

    public void testSimpleNew() throws Exception {
        setUpScript("testNewTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.simpleNew",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        final Customer customer = (Customer)getJellyContext().getVariable("foo");
        assertNull(customer.getName());
    }
}
