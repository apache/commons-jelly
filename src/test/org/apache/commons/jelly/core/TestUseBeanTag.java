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
package org.apache.commons.jelly.core;

import org.apache.commons.jelly.Script;
import junit.framework.TestSuite;

/**
 * Tests for UseBean tag
 */
public class TestUseBeanTag extends BaseJellyTest {

    public TestUseBeanTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestUseBeanTag.class);        
    }
    
    /**
     * Test a simple useBean tag works ok
     * @throws Exception
     */
    public void testSimple() throws Exception {
        setUpScript("testUseBeanTag.jelly");
        Script script = getJelly().compileScript();
        getJellyContext().setVariable("test.simple",Boolean.TRUE);
        script.run(getJellyContext(),getXMLOutput());
        assertNotNull(getJellyContext().getVariable("foo"));
        assertTrue(getJellyContext().getVariable("foo") instanceof Customer);
        Customer customer = (Customer)(getJellyContext().getVariable("foo"));
        assertEquals("name not set", "testing", customer.getName());
        assertEquals("city not set", "sydney", customer.getCity());
    }
}
