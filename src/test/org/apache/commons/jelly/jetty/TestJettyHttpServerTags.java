/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/test/org/apache/commons/jelly/TestJettyHttpServerTags.java,v 1.6 2002/05/15 07:47:50 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/15 07:47:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * $Id: TestJettyHttpServerTags.java,v 1.6 2002/05/15 07:47:50 jstrachan Exp $
 */
package org.apache.commons.jelly.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/** Tests the parser, the engine and the XML tags
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.6 $
  */
public class TestJettyHttpServerTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestJettyHttpServerTags.class);

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestJettyHttpServerTags.class);
    }

    public TestJettyHttpServerTags(String testName) {
        super(testName);
    }

    public void testDefaultServer() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/defaultServer.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testJettyLogFile() throws Exception {
        File logFile = new File("src/test/org/apache/commons/jelly/jetty/JellyLogFileTest.log");
        if (logFile.exists()) {
            logFile.delete();
        }
        assertTrue("Logfile does not exist", !logFile.exists());
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/jettyLogFile.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
        assertTrue("Logfile exists", logFile.exists());
    }

    public void testSocketListener() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/socketListener.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testHttpContext() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/httpContext.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testResourceHandler() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/resourceHandler.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testSecurityHandler() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/securityHandlerForbidden.jelly"
        );
        assertEquals("Forbidden test produces the correct output", "It works!", text);

        text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/securityHandlerUnauthorized.jelly"
        );
        assertEquals("Unauthorized produces the correct output", "It works!", text);
    }

    public void testJellyResourceHandler() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/jellyResourceHandler.jelly"
        );
        assertEquals("jellyResourceHandler produces the correct output", "It works!", text);

        text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/jetty/jellyResourceHandlerRequestBody.jelly"
        );
        assertEquals("jellyResourceHandlerRequestBody produces the correct output", "It works!", text);
    }

    /**
     * Evaluates the script by the given file name and
     * returns the whitespace trimmed output as text
     */
    protected String evaluteScriptAsText(String fileName) throws Exception {
        JellyContext context = new JellyContext();

        // allow scripts to refer to any resource inside this project
        // using an absolute URI like /src/test/org/apache/foo.xml
        context.setRootURL(new File(".").toURL());

        // cature the output
        StringWriter buffer = new StringWriter();
        XMLOutput output = XMLOutput.createXMLOutput(buffer);

        context.runScript( new File(fileName), output );
        String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        return text;
    }
}
