/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/test/org/apache/commons/jelly/TestXMLTags.java,v 1.6 2002/05/15 07:47:50 jstrachan Exp $
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
 * $Id: TestXMLTags.java,v 1.6 2002/05/15 07:47:50 jstrachan Exp $
 */
package org.apache.commons.jelly.xml;

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
public class TestXMLTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestXMLTags.class);

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestXMLTags.class);
    }

    public TestXMLTags(String testName) {
        super(testName);
    }

    public void testUnitTests() throws Exception {
        runUnitTest( "src/test/org/apache/commons/jelly/xml/testForEach.jelly" );
    }

    public void testExpressions() throws Exception {
        runUnitTest( "src/test/org/apache/commons/jelly/xml/testExpressions.jelly");
    }

    public void testParse() throws Exception {
        InputStream in = new FileInputStream("src/test/org/apache/commons/jelly/xml/example.jelly");
        XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        log.debug("Found: " + script);
        assertTrue("Parsed a Script", script instanceof Script);
        StringWriter buffer = new StringWriter();
        script.run(parser.getContext(), XMLOutput.createXMLOutput(buffer));
        String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransform() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformExample.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformAllInLine() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformExampleAllInLine.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformParams() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformParamExample.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformParamsInLine() throws Exception {

        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformParamExample2.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformSAXOutput() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformExampleSAXOutput.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformSAXOutputNestedTransforms() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformExampleSAXOutputNestedTransforms.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testTransformSchematron() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/schematron/transformSchematronExample.jelly"
        );
        assertEquals("Produces the correct output", "Report count=1:assert count=2", text);
    }

    public void testTransformXmlVar() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/transformExampleXmlVar.jelly"
        );
        assertEquals("Produces the correct output", "It works!", text);
    }

    public void testDoctype() throws Exception {
        String text = evaluteScriptAsText(
            "src/test/org/apache/commons/jelly/xml/testDoctype.jelly"
        );
        assertEquals("Produces the correct output", "<!DOCTYPE foo PUBLIC \"publicID\" \"foo.dtd\">\n<foo></foo>", text);
    }

    public void runUnitTest(String name) throws Exception {
        Document document = parseUnitTest(name);

        List failures = document.selectNodes( "/*/fail" );
        for ( Iterator iter = failures.iterator(); iter.hasNext(); ) {
            Node node = (Node) iter.next();
            fail( node.getStringValue() );
        }
    }

    public Document parseUnitTest(String name) throws Exception {
        // parse script
        InputStream in = new FileInputStream(name);
        XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        assertTrue("Parsed a Script", script instanceof Script);
        StringWriter buffer = new StringWriter();
        script.run(parser.getContext(), XMLOutput.createXMLOutput(buffer));

        String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }

        // now lets parse the output
        return DocumentHelper.parseText( text );
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
