/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.XMLWriter;

/** Tests the parser, the engine and the XML tags
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision$
  */
public class TestXMLTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestXMLTags.class);

    /** basedir for test source */
    private static final String testBaseDir ="src/test/org/apache/commons/jelly/tags/xml";

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
        runUnitTest( testBaseDir + "/testForEach.jelly" );
    }

    public void testExpressions() throws Exception {
        runUnitTest( testBaseDir + "/testExpressions.jelly");
    }

    public void testParse() throws Exception {
        InputStream in = new FileInputStream(testBaseDir + "/example.jelly");
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
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testElementWithNameSpace() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/elementWithNameSpace.jelly");
        assertEquals("Should produce the correct output",
                "<env:Envelope "+
                "xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
                "env:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
                "</env:Envelope>", text);
    }

    public void testElementWithNameSpaceError() throws Exception {
        try {
            evaluteScriptAsText(testBaseDir + "/elementWithNameSpaceError.jelly");
            Assert.fail("We should have bailed out with an JellyException");
        } catch (JellyException jex) {
            assertTrue(jex.getReason().startsWith("Cannot set same prefix to diferent URI in same node"));
        }
    }

    public void testNamespaceReplace() throws Exception {
        // For this test when we not set "ns" var with expected namespace, it
        // is expected to repeat the same two times
        String text = evaluteScriptAsText(testBaseDir + "/namespaceReplace.jelly");
        String repeatingText = "<test-subnode attr=\"test\"><test-anotherSubNode></test-anotherSubNode><test-anotherSubNodeAgain xmlns:other=\"\" other:abc=\"testValue\"></test-anotherSubNodeAgain></test-subnode>";
        assertEquals("Should produce the correct output",
                "<test-node xmlns:test=\"http://apache/testNS\" test:abc=\"testValue\">"+
                repeatingText + repeatingText +
                "</test-node>", text);

        Map ctxVars = new HashMap();
        ctxVars.put("ns", "http://java/ns");

        text = evaluteScriptAsText(testBaseDir + "/namespaceReplace.jelly", ctxVars);

        String firstTrunk =
        "<test-subnode xmlns=\"\" attr=\"test\">" +
        "<test-anotherSubNode>" +
        "</test-anotherSubNode>" +
        "<test-anotherSubNodeAgain xmlns:other=\"http://java/ns\" xmlns=\"http://java/ns\" other:abc=\"testValue\">" +
        "</test-anotherSubNodeAgain>" +
        "</test-subnode>";

        String secondTrunk =
            "<test-subnode attr=\"test\">" +
            "<test-anotherSubNode>" +
            "</test-anotherSubNode>" +
            "<test-anotherSubNodeAgain xmlns:other=\"http://java/ns\" other:abc=\"testValue\">" +
            "</test-anotherSubNodeAgain>" +
            "</test-subnode>";

        System.out.println("TestXMLTags.testNamespaceReplace() text="+text);
        assertEquals("Should produce the correct output",
                "<test-node xmlns:test=\"http://apache/testNS\" xmlns=\"http://java/ns\" test:abc=\"testValue\">"+
                firstTrunk + secondTrunk +
                "</test-node>", text);
    }

    public void testAttributeNameSpaceDuplicatedNS() throws Exception {
        try {
            evaluteScriptAsText(testBaseDir + "/attributeNameSpaceDuplicatedNS.jelly");
            Assert.fail("We should have bailed out with an JellyException");
        } catch (JellyException jex) {
            assertTrue(jex.getReason().startsWith("Cannot set same prefix to diferent URI in same node"));
        }
    }

    public void testAttributeNameSpace() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/attributeNameSpace.jelly");
        System.out.println(text);
        assertEquals("Should produce the correct output",
                "<top-node xmlns=\"abc\">"+
                "<test-node xmlns:test=\"http://apache/testNS\" xmlns=\"http://apache/trueNS\" test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">"+
                "<test:test-subnode><node-at-same-ns-as-top xmlns=\"abc\">"+
                "</node-at-same-ns-as-top>"+
                "</test:test-subnode>"+
                "</test-node>"+
                "</top-node>", text);
    }

    public void testAttributeNameSpaceDefaultNS() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/attributeNameSpaceDefaultNS.jelly");
        System.out.println(text);
        assertEquals("Should produce the correct output",
                "<top-node>"+
                "<test-node xmlns:test=\"http://apache/testNS\" xmlns=\"http://apache/trueNS\" test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">"+
                "<test:test-subnode><node-at-same-ns-as-top xmlns=\"\">"+
                "</node-at-same-ns-as-top>"+
                "</test:test-subnode>"+
                "</test-node>"+
                "</top-node>", text);
    }

    public void testAttributeNameSpaceWithInnerElements() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/attributeNameSpaceWithInnerElements.jelly");
        assertEquals("Should produce the correct output",
                "<test-node xmlns:test=\"http://apache/testNS\" test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">"+
                "<test-sub-node xmlns:test2=\"http://apache/testNS\" xmlns:test3=\"http://apache/anotherNS\" test:abc=\"testValue\" test2:abc2=\"testValue\" test3:abc3=\"testValue\">"+
                "</test-sub-node>"+
                "</test-node>"
                , text);
    }

    public void testTransform() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/transformExample.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformAllInLine() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/transformExampleAllInLine.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformParams() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/transformParamExample.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformParamsInLine() throws Exception {

        String text = evaluteScriptAsText(testBaseDir + "/transformParamExample2.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSAXOutput() throws Exception {
        String text = evaluteScriptAsText(testBaseDir + "/transformExampleSAXOutput.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSAXOutputNestedTransforms() throws Exception {
        String text = evaluteScriptAsText(testBaseDir +
            "/transformExampleSAXOutputNestedTransforms.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSchematron() throws Exception {
        String text = evaluteScriptAsText(testBaseDir +
            "/schematron/transformSchematronExample.jelly");
        assertEquals("Should produce the correct output", "Report count=1:assert count=2", text);
    }

    public void testTransformXmlVar() throws Exception {
        String text = evaluteScriptAsText(testBaseDir +
            "/transformExampleXmlVar.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testDoctype() throws Exception {
        String text = evaluteScriptAsText(testBaseDir +
            "/testDoctype.jelly");
        assertEquals("Should produce the correct output", "<!DOCTYPE foo PUBLIC \"publicID\" \"foo.dtd\">\n<foo></foo>", text);
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
        return evaluteScriptAsText(fileName, null);
    }

    /**
     * Evaluates the script by the given file name and
     * returns the whitespace trimmed output as text
     */
    protected String evaluteScriptAsText(String fileName, Map ctxVars) throws Exception {
        JellyContext context = new JellyContext();
        if (ctxVars != null) {
            Set keys = ctxVars.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                Object value = ctxVars.get(key);
                context.setVariable(key, value);
            }
        }

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

    protected String evaluteScriptAsTextUsingSaxContentHandler(String fileName, Map ctxVars) throws Exception {
        org.dom4j.io.OutputFormat outputFormat = new org.dom4j.io.OutputFormat();
        outputFormat.setSuppressDeclaration(true);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        outputFormat.setExpandEmptyElements(true);
        //outputFormat.setIndentSize(4);

        StringWriter buffer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(buffer, outputFormat);
        // xmlWriter.setEscapeText(false);

        SAXContentHandler saxHandler = new SAXContentHandler();
        XMLOutput output = new XMLOutput(saxHandler);

        // now run a script using a URL
        JellyContext context = new JellyContext();
        if (ctxVars != null) {
            Set keys = ctxVars.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                Object value = ctxVars.get(key);
                context.setVariable(key, value);
            }
        }

        // allow scripts to refer to any resource inside this project
        // using an absolute URI like /src/test/org/apache/foo.xml
        context.setRootURL(new File(".").toURL());

        output.startDocument();
        context.runScript(new File(fileName), output);
        output.endDocument();
        xmlWriter.write(saxHandler.getDocument());
        xmlWriter.flush();

        String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        return text;
    }

}
