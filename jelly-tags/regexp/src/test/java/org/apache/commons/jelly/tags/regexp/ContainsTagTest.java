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
package org.apache.commons.jelly.tags.regexp;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;

/*** <p><code>ContainsTagTest</code> a class that is useful to perform regexp matches
* in strings.</p>
*/
public class ContainsTagTest extends TestCase {

    public ContainsTagTest(final String name)
    {
      super(name);
    }

    @Override
    public void setUp() throws Exception
    {
    }

    @Override
    public void tearDown()
    {
    }

    public void testDoTag() throws Exception
    {
      final ContainsTag containsExpTag = new ContainsTag();
      final XMLOutput xmlOutput = new XMLOutput();

      containsExpTag.setText("Hello World");
      containsExpTag.setExpr("World");
      containsExpTag.setVar("testvar");
      containsExpTag.setContext(new JellyContext());
      containsExpTag.doTag(xmlOutput);

      assertEquals("TRUE", containsExpTag.getContext().getVariable("testvar").toString().toUpperCase());
    }

}
