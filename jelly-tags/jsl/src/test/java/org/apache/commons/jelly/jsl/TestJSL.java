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
package org.apache.commons.jelly.jsl;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXContentHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests the JSL tags.
 * Note this test harness could be written in Jelly script
 * if we had the junit tag library!
 */
public class TestJSL extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestJSL.class);

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestJSL.class);
    }

    public TestJSL(final String testName) {
        super(testName);
    }

    protected Document runScript(final String fileName) throws Exception {
        final InputStream in = new FileInputStream(fileName);
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        final JellyContext context = parser.getContext();

        final SAXContentHandler contentHandler = new SAXContentHandler();
        final XMLOutput output = new XMLOutput( contentHandler );

        contentHandler.startDocument();
        script.run(context, output);
        contentHandler.endDocument();

        return contentHandler.getDocument();
    }

    public void testExample1() throws Exception {
        final Document document = runScript( "target/test-classes/org/apache/commons/jelly/jsl/example.jelly" );
        final Element small = (Element) document.selectSingleNode("/html/body/small");

        //assertTrue( "<small> starts with 'James Elson'", small.getText().trim().startsWith("James Elson") );
        assertEquals( "I am a title!", small.valueOf( "h2" ).trim() );
        assertEquals( "Twas a dark, rainy night...", small.valueOf( "small" ).trim() );
        assertEquals( "dfjsdfjsdf", small.valueOf( "p" ).trim() );
    }
}
