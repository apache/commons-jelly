/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/core/TestNewTag.java,v 1.3 2003/01/15 12:23:20 dion Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/15 12:23:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: TestNewTag.java,v 1.3 2003/01/15 12:23:20 dion Exp $
 */
package org.apache.commons.jelly.core;

import java.util.Date;

import junit.framework.TestSuite;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.core.Customer;

/**
 * @author Rodney Waldhoff
 * @version $Revision: 1.3 $ $Date: 2003/01/15 12:23:20 $
 */
public class TestNewTag extends BaseJellyTest {

    public TestNewTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestNewTag.class);        
    }

    public void setUp() throws Exception {
        super.setUp();
    }    
    
    public void tearDown() throws Exception {
        super.tearDown();
    }    
    
    public void testSimpleNew() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.simpleNew",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertNull(customer.getName());
    }

    public void testNewThenOverwrite() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newThenOverwrite",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Date);
    }

    public void testNewWithLiteralArg() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithLiteralArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
    }

    public void testNewWithTwoArgs() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithTwoArgs",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
        assertNotNull(customer.getCity());
        assertEquals("Chicago",customer.getCity());
    }

    public void testNewWithExpressionArg() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithExpressionArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertNotNull(customer.getName());
        assertEquals("Jane Doe",customer.getName());
    }

    public void testNewWithNullArg() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithNullArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertNull(customer.getName());
    }

    public void testNewWithNewArg() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithNewArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        {
            assertNotNull(getJellyContext().getVariable("foo"));
            assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
            Customer customer = (Customer)(getJellyContext().getVariable("foo"));
            assertNotNull(customer.getName());
            assertEquals("",customer.getName());
        }
        {
            assertNotNull(getJellyContext().getVariable("bar"));
            assertTrue(getJellyContext().getVariable("bar") instanceof Customer);
            Customer customer = (Customer)(getJellyContext().getVariable("bar"));
            assertEquals("Jane Doe",customer.getName());
            assertEquals("Chicago",customer.getCity());
            assertNotNull(customer.getOrders());
            assertEquals(1,customer.getOrders().size());
            assertNotNull(customer.getOrders().get(0));
        }
        {
            assertNotNull(getJellyContext().getVariable("qux"));
            assertTrue(getJellyContext().getVariable("qux") instanceof Customer);
            Customer customer = (Customer)(getJellyContext().getVariable("qux"));
            assertEquals("Jane Doe",customer.getName());
            assertEquals("Chicago",customer.getCity());
            assertNotNull(customer.getOrders());
            assertEquals(1,customer.getOrders().size());
            assertNotNull(customer.getOrders().get(0));
        }
    }

    public void testNewWithUseBeanArg() throws Exception {
        setUpScript("testNewTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.newWithUseBeanArg",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertEquals("Jane Doe",customer.getName());
        assertEquals("Chicago",customer.getCity());
        assertEquals("Location",customer.getLocation());
    }
}
