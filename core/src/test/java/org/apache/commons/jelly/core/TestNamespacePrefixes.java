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
package org.apache.commons.jelly.core;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TJTagLibrary;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.jelly.test.BaseJellyTest;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import junit.framework.TestSuite;

/**
 * Tests for bug JELLY-184, where enabling feature
 * "http://xml.org/sax/features/namespace-prefixes" breaks Jelly.
 * 
 * @author <a href="mailto:john.spackman@zenesis.net">John Spackman</a>
 */
public class TestNamespacePrefixes extends BaseJellyTest {

	public TestNamespacePrefixes(String name) {
		super(name);
	}

	public static TestSuite suite() throws Exception {
		return new TestSuite(TestNamespacePrefixes.class);
	}

    protected void addCustomTagLib(JellyContext context) {
        context.registerTagLibrary(TJTagLibrary.NS, TJTagLibrary.class.getName());
    }

    

	public void testNamespacePrefixes() throws Exception {
		SAXParserFactory pf = SAXParserFactory.newInstance();
		pf.setValidating(false);
		pf.setNamespaceAware(true);
		pf.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		XMLReader reader = null;
		SAXParser parser = pf.newSAXParser();
		reader = parser.getXMLReader();

        URL url = this.getClass().getResource("testNamespacePrefixes.xml");
        if (null == url)
            throw new Exception("Could not find Jelly script: testNamespacePrefixes.xml in package of class: " + getClass().getName());
		InputSource inSrc = new InputSource(new FileReader(new File(url.getPath())));

		JellyContext context = getJellyContext();
		XMLParser jellyParser = new XMLParser();
		jellyParser.setContext(context);

		reader.setContentHandler(jellyParser);
		reader.parse(inSrc);

		Script script = jellyParser.getScript();
		script.compile();
		script.run(context, getXMLOutput());
		
		String str = getStringOutput().toString();
		System.out.println(str);
	}
}
