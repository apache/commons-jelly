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

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.test.BaseJellyTest;

import junit.framework.TestSuite;

/*
 */
public class TestSwitchTag extends BaseJellyTest {

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestSwitchTag.class);
    }

    public TestSwitchTag(final String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCaseAfterDefault() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("case.after.default",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected JellyException");
        } catch (final JellyException e) {
            // expected
        }
    }

    public void testCaseWithoutSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("case.without.switch",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected JellyException");
        } catch (final JellyException e) {
            // expected
        }
    }

    public void testCaseWithoutValue() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("case.without.value",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected MissingAttributeException");
        } catch (final MissingAttributeException e) {
            // expected
        }
    }

    public void testDefault() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("switch.on.a","negative one");
        script.run(getJellyContext(),getXMLOutput());
        assertNull("should not have 'a.one' variable set",
                   getJellyContext().getVariable("a.one"));
        assertNull("should not have 'a.two' variable set",
                   getJellyContext().getVariable("a.two"));
        assertNull("should not have 'a.three' variable set",
                   getJellyContext().getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   getJellyContext().getVariable("a.null"));
        assertTrue("should have set 'a.default' variable to 'true'",
                   getJellyContext().getVariable("a.default").equals("true"));
    }

    public void testDefaultWithoutSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("default.without.switch",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected JellyException");
        } catch (final JellyException e) {
            // expected
        }
    }

    public void testFallThru() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("switch.on.a","one");
        script.run(getJellyContext(),getXMLOutput());
        assertTrue("should have set 'a.one' variable to 'true'",
                   getJellyContext().getVariable("a.one").equals("true"));
        assertTrue("should have set 'a.two' variable to 'true'",
                   getJellyContext().getVariable("a.two").equals("true"));
        assertNull("should not have 'a.three' variable set",
                   getJellyContext().getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   getJellyContext().getVariable("a.null"));
        assertNull("should not have 'a.default' variable set",
                   getJellyContext().getVariable("a.default"));
    }

    public void testMultipleDefaults() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("multiple.defaults",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected JellyException");
        } catch (final JellyException e) {
            // expected
        }
    }

    public void testNullCase() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("switch.on.a",null);
        script.run(getJellyContext(),getXMLOutput());
        assertNull("should not have 'a.one' variable set",
                   getJellyContext().getVariable("a.one"));
        assertNull("should not have 'a.two' variable set",
                   getJellyContext().getVariable("a.two"));
        assertNull("should not have 'a.three' variable set",
                   getJellyContext().getVariable("a.three"));
        assertTrue("should have set 'a.null' variable to 'true'",
                   getJellyContext().getVariable("a.null").equals("true"));
        assertNull("should not have 'a.default' variable set",
                   getJellyContext().getVariable("a.default"));
    }

    public void testSeveralCall() throws Exception {
        setUpScript("testSeveralSwitchCall.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("var","foo");
        script.run(getJellyContext(),getXMLOutput());
        assertEquals("defaultdefault",getJellyContext().getVariable("res"));
    }

    public void testSimpleSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("switch.on.a","two");
        script.run(getJellyContext(),getXMLOutput());
        assertNull("should not have 'a.one' variable set",
                   getJellyContext().getVariable("a.one"));
        assertTrue("should have set 'a.two' variable to 'true'",
                   getJellyContext().getVariable("a.two").equals("true"));
        assertNull("should not have 'a.three' variable set",
                   getJellyContext().getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   getJellyContext().getVariable("a.null"));
        assertNull("should not have 'a.default' variable set",
                   getJellyContext().getVariable("a.default"));
    }

    public void testSwitchWithoutOn() throws Exception {
        setUpScript("testSwitchTag.jelly");
        final Script script = getJelly().compileScript();
        getJellyContext().setVariable("switch.without.on",new Boolean(true));
        try {
            script.run(getJellyContext(),getXMLOutput());
            fail("Expected MissingAttributeException");
        } catch (final MissingAttributeException e) {
            // expected
        }
    }

}
