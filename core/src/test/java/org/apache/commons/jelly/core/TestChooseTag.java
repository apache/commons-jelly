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

/*
 */
public class TestChooseTag extends BaseJellyTest
{

    public static TestSuite suite() throws Exception
    {
        return new TestSuite(TestChooseTag.class);
    }

    public TestChooseTag(final String name)
    {
        super(name);
    }

    public void testSimpleFileTag() throws Exception
    {
        setUpScript("testChooseTag.jelly");
        final Script script = getJelly().compileScript();

        script.run(getJellyContext(), getXMLOutput());

        final String resultTrue = (String) getJellyContext().getVariable("result.true");
        final String resultFalse = (String) getJellyContext().getVariable("result.false");

        assertEquals("result.true", "AC", resultTrue);
        assertEquals("result.false", "BC", resultFalse);
    }

}
