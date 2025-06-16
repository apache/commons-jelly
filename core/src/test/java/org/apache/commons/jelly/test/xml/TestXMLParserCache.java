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
package org.apache.commons.jelly.test.xml;

import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test to confirm that invalid documents are
 * reject iff jelly.setValidateXML(true)
 */
public class TestXMLParserCache extends TestCase {

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestXMLParserCache.class);
    }
    Jelly jelly = null;
    JellyContext context = null;

    XMLOutput xmlOutput = null;

    public TestXMLParserCache(final String name) {
        super(name);
    }

    public void setUp(final String scriptName) throws Exception {
        context = new JellyContext();
        xmlOutput = XMLOutput.createXMLOutput(new StringWriter());

        jelly = new Jelly();

        final String script = scriptName;
        final URL url = this.getClass().getResource(script);
        if ( url == null ) {
            throw new Exception(
                "Could not find Jelly script: " + script
                + " in package of class: " + this.getClass().getName()
            );
        }
        jelly.setUrl(url);
    }

    public void testParserCache1() throws Exception {
        // without validation, should
        // not fail because validation is disabled
        setUp("invalidScript1.jelly");
        jelly.setValidateXML(false);
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'foo' variable to 'bar'",
                   context.getVariable("foo").equals("bar"));

        // if I enable XML validation, the script should fail
        // despite the cache
        jelly.setValidateXML(true);
        try {
            script = jelly.compileScript();
            fail("Invalid scripts should throw JellyException on parse, despite the cache");
        } catch (final JellyException e) {
        }
    }

    public void testParserCache2() throws Exception {
        // no default namespace
        setUp("nsFilterTest.jelly");
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have no var when default namspace is not set",
                   context.getVariable("usedDefaultNamespace") == null);

        // now we have a default namespace, so we
        // should see a variable, despite the XMLParser cache
        jelly.setDefaultNamespaceURI("jelly:core");
        script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have var when default namspace is set",
                   context.getVariable("usedDefaultNamespace").equals("true"));
    }
}
