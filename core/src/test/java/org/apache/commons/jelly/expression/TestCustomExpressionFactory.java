/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.expression;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.*;
import org.apache.commons.jelly.test.BaseJellyTest;

public class TestCustomExpressionFactory extends BaseJellyTest {
	
	private static final String EXPECTED = "id=1; attr=${TEST FACTORY:  1 + 2 }; text=${TEST FACTORY:  'hello' + \" world\" }\n" + 
				"id=2; attr=${TEST FACTORY:  2 + 3 }; text=<sometag>${TEST FACTORY:  'goodbye cruel' + \" world\" }</sometag>\n";

	public TestCustomExpressionFactory() {
		super("TestCustomExpressionFactory");
	}
	
    protected void addCustomTagLib(JellyContext context) {
        context.registerTagLibrary(TJTagLibrary.NS, TJTagLibrary.class.getName());
    }


	public void testCustomFactory() throws Exception {
		setUpScript("jelly1.xml");
		Script script = getJelly().compileScript();
		script.run(getJellyContext(), getXMLOutput());

		/*
		JellyContext ctx = new JellyContext();
		ctx.registerTagLibrary(TJTagLibrary.NS, TJTagLibrary.class.getName());
		
		URL url = getClass().getResource("jelly1.xml");
		if (url == null)
			throw new Exception("Cannot find jelly1.xml in classpath");
		File file = new File(url.getPath());
		StringWriter strWriter = new StringWriter();
		XMLOutput output = XMLOutput.createXMLOutput(strWriter);
		ctx.runScript(file, output);
		
		*/
		String str = getStringOutput().toString();
		System.out.println(str);
		assertEquals(str, EXPECTED); 
	}

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestCustomExpressionFactory.class);
    }

}
