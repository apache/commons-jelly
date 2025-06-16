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

public class TestBreakTag extends BaseJellyTest
{

    public static TestSuite suite() throws Exception
    {
        return new TestSuite(TestBreakTag.class);
    }

    public TestBreakTag(final String name)
    {
        super(name);
    }

    public void testConditionalBreakTag() throws Exception
    {
        setUpScript("testBreakTag.jelly");
        final Script script = getJelly().compileScript();

        script.run(getJellyContext(), getXMLOutput());

        final String simpleResult = (String) getJellyContext().getVariable("conditionalResult");

        assertEquals("conditionalResult", "12345", simpleResult);
    }

    public void testSimpleBreakTag() throws Exception
    {
        setUpScript("testBreakTag.jelly");
        final Script script = getJelly().compileScript();

        script.run(getJellyContext(), getXMLOutput());

        final String simpleResult = (String) getJellyContext().getVariable("simpleResult");

        assertEquals("simpleResult", "12345", simpleResult);
    }

    public void testVarBreakTag() throws Exception
    {
        setUpScript("testBreakTag.jelly");
        final Script script = getJelly().compileScript();

        script.run(getJellyContext(), getXMLOutput());

        final String varBroken = (String) getJellyContext().getVariable("varBroken");

        assertEquals("varBroken", "true", varBroken);
    }

    public void testVarNoBreakTag() throws Exception
    {
        setUpScript("testBreakTag.jelly");
        final Script script = getJelly().compileScript();

        script.run(getJellyContext(), getXMLOutput());

        final String varNotBroken = (String) getJellyContext().getVariable("varNotBroken");

        assertEquals("varNotBroken", "false", varNotBroken);
    }

}
