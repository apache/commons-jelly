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
package org.apache.commons.jelly.jetty;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests the parser, the engine and the XML tags
  */
public class TestJettyHttpServerTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestJettyHttpServerTags.class);

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestJettyHttpServerTags.class);
    }

    public TestJettyHttpServerTags(final String testName) {
        super(testName);
    }

    /**
     * Evaluates the script by the given file name and
     * returns the whitespace trimmed output as text
     */
    protected String evaluateScriptAsText(final String fileName) throws Exception {
        final JellyContext context = new JellyContext();

        final URL scriptUrl = getClass().getResource(fileName);
        // allow scripts to refer to any resource inside this project
        // using an absolute URI like /src/test/org/apache/foo.xml
        context.setRootURL(scriptUrl);

        // capture the output
        final StringWriter buffer = new StringWriter();
        final XMLOutput output = XMLOutput.createXMLOutput(buffer);

        context.runScript( scriptUrl, output );
        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        return text;
    }

    public void testDefaultServer() throws Exception {
        final String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/defaultServer.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testHttpContext() throws Exception {
        final String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/httpContext.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testJellyResourceHandler() throws Exception {
        String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/jellyResourceHandler.jelly"
        );
        assertEquals("jellyResourceHandler produces the correct output", "It works!", text);

        text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/jellyResourceHandlerRequestBody.jelly"
        );
        assertEquals("jellyResourceHandlerRequestBody produces the correct output", "It works!", text);
    }

    public void testJettyLogFile() throws Exception {
        final File logFile = new File("target/test-classes/org/apache/commons/jelly/jetty/JellyLogFileTest.log");
        if (logFile.exists()) {
            logFile.delete();
        }
        assertTrue("Logfile does not exist", !logFile.exists());
        final String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/jettyLogFile.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
        assertTrue("Logfile exists", logFile.exists());
    }

    public void testResourceHandler() throws Exception {
        final String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/resourceHandler.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testSecurityHandler() throws Exception {
        String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/securityHandlerForbidden.jelly"
        );
        assertEquals("Forbidden test produces the correct output", "It works!", text);

        text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/securityHandlerUnauthorized.jelly"
        );
        assertEquals("Unauthorized produces the correct output", "It works!", text);
    }

    public void testSocketListener() throws Exception {
        final String text = evaluateScriptAsText(
            "/org/apache/commons/jelly/jetty/socketListener.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }
}
