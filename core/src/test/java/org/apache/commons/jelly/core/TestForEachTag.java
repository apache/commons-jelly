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
import org.apache.commons.lang3.StringUtils;

import junit.framework.TestSuite;

/*
 */
public class TestForEachTag extends BaseJellyTest
{

    public static TestSuite suite() throws Exception
    {
        return new TestSuite(TestForEachTag.class);
    }

    public TestForEachTag(final String name)
    {
        super(name);
    }

    public void testForEachTag() throws Exception
    {
        setUpScript("testForEachTag.jelly");
        final Script script = getJelly().compileScript();

        getJellyContext().setVariable("myList",
              new Object[] {"0", "VOID", "1", "VOID", "2", "VOID",
                            "3", "VOID", "4", "VOID", "5"});
        getJellyContext().setVariable("testMyList", Boolean.TRUE);
        script.run(getJellyContext(), getXMLOutput());

        String resultOrdered =
                (String) getJellyContext().getVariable("result.ordered");
        System.err.println("raw result is '" + resultOrdered + "'");
        resultOrdered = StringUtils.replace(resultOrdered, " ", "");
        resultOrdered = StringUtils.replace(resultOrdered, "\n", "");

        assertEquals("result.ordered",
                       "FIRST_262_121/MIDDLE_242/LAST_363/",
                     resultOrdered);
    }

    public void testForEachTagNumList() throws Exception
    {
        setUpScript("testForEachTag.jelly");
        final Script script = getJelly().compileScript();

        getJellyContext().setVariable("testNumList", Boolean.TRUE);
        script.run(getJellyContext(), getXMLOutput());

        String resultOrdered =
                (String) getJellyContext().getVariable("result.ordered");
        System.err.println("raw result is '" + resultOrdered + "'");
        resultOrdered = StringUtils.replace(resultOrdered, " ", "");
        resultOrdered = StringUtils.replace(resultOrdered, "\n", "");

        assertEquals("result.ordered",
                       "FIRST_262_122/MIDDLE_244/LAST_366/",
                     resultOrdered);
    }
}
