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
package org.apache.commons.jelly;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.jelly.impl.TextScript;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests the core tags
  */
public class TestCoreTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestCoreTags.class);

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestCoreTags.class);
    }

    public TestCoreTags(final String testName) {
        super(testName);
    }

    public void testArgs() throws Exception {
        final InputStream in = new FileInputStream("src/test/resources/org/apache/commons/jelly/test_args.jelly");
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        log.debug("Found: " + script);
        final String[] args = { "one", "two", "three" };
        final JellyContext context = new JellyContext();
        context.setVariable("args", args);
        final StringWriter buffer = new StringWriter();
        script.run(context, XMLOutput.createXMLOutput(buffer));
        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        assertEquals("Produces the correct output", "one two three", text);
    }

    public void testStaticNamespacedAttributes() throws Exception {
        final InputStream in = new FileInputStream("src/test/resources/org/apache/commons/jelly/testStaticNamespacedAttributes.jelly");
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        log.debug("Found: " + script);
        final JellyContext context = new JellyContext();
        final StringWriter buffer = new StringWriter();
        script.run(context, XMLOutput.createXMLOutput(buffer));
        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        assertEquals("Should produces the correct output",
                "<blip xmlns:blop=\"blop\" blop:x=\"blip\"></blip>",
                text);
    }

    public void testTrimEndWhitespace() throws Exception {
        TextScript textScript = new TextScript(" ");
        textScript.trimEndWhitespace();
        assertEquals("", textScript.getText());

        textScript = new TextScript("");
        textScript.trimEndWhitespace();
        assertEquals("", textScript.getText());

        textScript = new TextScript(" foo ");
        textScript.trimEndWhitespace();
        assertEquals(" foo", textScript.getText());

        textScript = new TextScript("foo");
        textScript.trimEndWhitespace();
        assertEquals("foo", textScript.getText());
    }

    public void testTrimStartWhitespace() throws Exception {
        TextScript textScript = new TextScript(" ");
        textScript.trimStartWhitespace();
        assertEquals("", textScript.getText());

        textScript = new TextScript("");
        textScript.trimStartWhitespace();
        assertEquals("", textScript.getText());

        textScript = new TextScript(" foo ");
        textScript.trimStartWhitespace();
        assertEquals("foo ", textScript.getText());

        textScript = new TextScript("foo");
        textScript.trimStartWhitespace();
        assertEquals("foo", textScript.getText());
    }
}
