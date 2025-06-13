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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests the parser, the engine and the XML tags
  */
public class TestXMLTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestXMLTags.class);

    /** Basedir for test source */
    private static final String testBaseDir ="target/test-classes/org/apache/commons/jelly/tags/xml";

    /** Regular expression for multiple namespace attributes. */
    private static final String REG_NS = "( xmlns(:(\\w)+)?=\".+\")+";

    /**
     * Checks an XML fragment with a namespace expression. Namespaces are
     * generated in arbitrary order. Therefore, they cannot be checked
     * directly.
     * @param s the fragment to be checked
     * @param pre the part before the namespace expression
     * @param post the part after the namespace expression
     * @return the extracted namespace expression
     */
    private static String checkNamespaceFragment(final String s, final String pre, final String post) {
        final Matcher matcher = matcherForNamespaceFragment(s, pre, post);
        assertTrue("Pattern '" + pre + REG_NS + post + "' does not match " + s,
                matcher.matches());
        return matcher.group(1);
    }

    /**
     * Tries to find a fragment with the specified namespaces in the given
     * string.
     * @param s the string
     * @param pre the part before the namespace expression
     * @param post the part after the namespace expression
     * @param ns the namespaces to be matched in the fragment
     */
    private static void findNamespaceFragment(final String s, final String pre, final String post,
                                                final String... ns) {
        final Matcher matcher = matcherForNamespaceFragment(s, pre, post);
        assertTrue("Pattern '" + pre + REG_NS + post + "' does not match " + s,
                matcher.find());
        final String match = matcher.group(1);
        for (final String namespace : ns) {
            assertTrue("Namespace '" + namespace + "' not found in '" + match + "':",
                    match.contains(namespace));
        }
    }

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Returns a matcher for a namespace XML fragment.
     * @param s the fragment to be checked
     * @param pre the part before the namespace expression
     * @param post the part after the namespace expression
     * @return the matcher
     */
    private static Matcher matcherForNamespaceFragment(final String s, final String pre, final String post) {
        final Pattern pattern = Pattern.compile(Pattern.quote(pre) + REG_NS + Pattern.quote(post));
        return pattern.matcher(s);
    }

    public static Test suite() {
        return new TestSuite(TestXMLTags.class);
    }

    public TestXMLTags(final String testName) {
        super(testName);
    }

    /**
     * Evaluates the script by the given file name and
     * returns the whitespace trimmed output as text
     */
    protected String evaluateScriptAsText(final String fileName) throws Exception {
        return evaluateScriptAsText(fileName, null);
    }

    /**
     * Evaluates the script by the given file name and
     * returns the whitespace trimmed output as text
     */
    protected String evaluateScriptAsText(final String fileName, final Map ctxVars) throws Exception {
        final JellyContext context = new JellyContext();
        if (ctxVars != null) {
            final Set keys = ctxVars.keySet();
            for (final Iterator iterator = keys.iterator(); iterator.hasNext();) {
                final String key = (String) iterator.next();
                final Object value = ctxVars.get(key);
                context.setVariable(key, value);
            }
        }

        // allow scripts to refer to any resource inside this project
        // using an absolute URI like /src/test/org/apache/foo.xml
        context.setRootURL(new File(".").toURL());

        // capture the output
        final StringWriter buffer = new StringWriter();
        final XMLOutput output = XMLOutput.createXMLOutput(buffer);

        context.runScript( new File(fileName), output );
        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        return text;
    }

    protected String evaluateScriptAsTextUsingSaxContentHandler(final String fileName, final Map ctxVars) throws Exception {
        final org.dom4j.io.OutputFormat outputFormat = new org.dom4j.io.OutputFormat();
        outputFormat.setSuppressDeclaration(true);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        outputFormat.setExpandEmptyElements(true);
        //outputFormat.setIndentSize(4);

        final StringWriter buffer = new StringWriter();
        final XMLWriter xmlWriter = new XMLWriter(buffer, outputFormat);
        // xmlWriter.setEscapeText(false);

        final SAXContentHandler saxHandler = new SAXContentHandler();
        final XMLOutput output = new XMLOutput(saxHandler);

        // now run a script using a URL
        final JellyContext context = new JellyContext();
        if (ctxVars != null) {
            final Set keys = ctxVars.keySet();
            for (final Iterator iterator = keys.iterator(); iterator.hasNext();) {
                final String key = (String) iterator.next();
                final Object value = ctxVars.get(key);
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

        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        return text;
    }

    public Document parseUnitTest(final String name) throws Exception {
        // parse script
        final InputStream in = new FileInputStream(name);
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        assertTrue("Parsed a Script", script instanceof Script);
        final StringWriter buffer = new StringWriter();
        script.run(parser.getContext(), XMLOutput.createXMLOutput(buffer));

        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }

        // now lets parse the output
        return DocumentHelper.parseText( text );
    }

    public void runUnitTest(final String name) throws Exception {
        final Document document = parseUnitTest(name);

        final List failures = document.selectNodes( "/*/fail" );
        for (Object failure : failures) {
            final Node node = (Node) failure;
            fail(node.getStringValue());
        }
    }

    public void testAttributeNameSpace() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/attributeNameSpace.jelly");
        System.out.println(text);
        final String ns = checkNamespaceFragment(text, "<top-node xmlns=\"abc\"><test-node",
                " test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">"+
                        "<test:test-subnode><node-at-same-ns-as-top xmlns=\"abc\">"+
                        "</node-at-same-ns-as-top>"+
                        "</test:test-subnode>"+
                        "</test-node>"+
                        "</top-node>");
        assertTrue(ns.contains("xmlns:test=\"http://apache/testNS\""));
        assertTrue(ns.contains("xmlns=\"http://apache/trueNS\""));
    }

    public void testAttributeNameSpaceDefaultNS() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/attributeNameSpaceDefaultNS.jelly");
        System.out.println(text);
        final String ns = checkNamespaceFragment(text, "<top-node><test-node",
                " test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">" +
                        "<test:test-subnode><node-at-same-ns-as-top xmlns=\"\">"+
                        "</node-at-same-ns-as-top>"+
                        "</test:test-subnode>"+
                        "</test-node>"+
                        "</top-node>");
        assertTrue(ns.contains("xmlns:test=\"http://apache/testNS\""));
        assertTrue(ns.contains("xmlns=\"http://apache/trueNS\""));
    }

    public void testAttributeNameSpaceDuplicatedNS() throws Exception {
        try {
            evaluateScriptAsText(testBaseDir + "/attributeNameSpaceDuplicatedNS.jelly");
            Assert.fail("We should have bailed out with an JellyException");
        } catch (final JellyException jex) {
            assertTrue(jex.getReason().startsWith("Cannot set same prefix to different URI in same node"));
        }
    }

    public void testAttributeNameSpaceWithInnerElements() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/attributeNameSpaceWithInnerElements.jelly");
        assertEquals("Should produce the correct output",
                "<test-node xmlns:test=\"http://apache/testNS\" test:abc=\"testValue\" abc2=\"testValue\" abc3=\"testValue\">"+
                "<test-sub-node xmlns:test2=\"http://apache/testNS\" xmlns:test3=\"http://apache/anotherNS\" test:abc=\"testValue\" test2:abc2=\"testValue\" test3:abc3=\"testValue\">"+
                "</test-sub-node>"+
                "</test-node>"
                , text);
    }

    public void testDoctype() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir +
            "/testDoctype.jelly");
        assertEquals("Should produce the correct output", "<!DOCTYPE foo PUBLIC \"publicID\" \"foo.dtd\">\n<foo></foo>", text);
    }

    public void testElementWithNameSpace() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/elementWithNameSpace.jelly");
        assertEquals("Should produce the correct output",
                "<env:Envelope "+
                "xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
                "env:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
                "</env:Envelope>", text);
    }

    public void testElementWithNameSpaceError() throws Exception {
        try {
            evaluateScriptAsText(testBaseDir + "/elementWithNameSpaceError.jelly");
            Assert.fail("We should have bailed out with an JellyException");
        } catch (final JellyException jex) {
            assertTrue(jex.getReason().startsWith("Cannot set same prefix to different URI in same node"));
        }
    }

    public void testExpressions() throws Exception {
        runUnitTest( testBaseDir + "/testExpressions.jelly");
    }

    public void testNamespaceReplace() throws Exception {
        // For this test when we not set "ns" var with expected namespace, it
        // is expected to repeat the same two times
        String text = evaluateScriptAsText(testBaseDir + "/namespaceReplace.jelly");
        final String repeatingText = "<test-subnode attr=\"test\"><test-anotherSubNode></test-anotherSubNode><test-anotherSubNodeAgain xmlns:other=\"\" other:abc=\"testValue\"></test-anotherSubNodeAgain></test-subnode>";
        assertEquals("Should produce the correct output",
                "<test-node xmlns:test=\"http://apache/testNS\" test:abc=\"testValue\">"+
                repeatingText + repeatingText +
                "</test-node>", text);

        final Map ctxVars = new HashMap();
        ctxVars.put("ns", "http://java/ns");

        text = evaluateScriptAsText(testBaseDir + "/namespaceReplace.jelly", ctxVars);

        findNamespaceFragment(text,
                "<test-subnode xmlns=\"\" attr=\"test\">"
                        + "<test-anotherSubNode>" + "</test-anotherSubNode>"
                        + "<test-anotherSubNodeAgain",
                " other:abc=\"testValue\">" + "</test-anotherSubNodeAgain>"
                        + "</test-subnode>" + "<test-subnode attr=\"test\">"
                        + "<test-anotherSubNode>" + "</test-anotherSubNode>"
                        + "<test-anotherSubNodeAgain xmlns:other=\"http://java/ns\" other:abc=\"testValue\">"
                        + "</test-anotherSubNodeAgain>" + "</test-subnode>",
                "xmlns:other=\"http://java/ns\"", "xmlns=\"http://java/ns\"");
    }

    public void testParse() throws Exception {
        final InputStream in = new FileInputStream(testBaseDir + "/example.jelly");
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        log.debug("Found: " + script);
        assertTrue("Parsed a Script", script instanceof Script);
        final StringWriter buffer = new StringWriter();
        script.run(parser.getContext(), XMLOutput.createXMLOutput(buffer));
        final String text = buffer.toString().trim();
        if (log.isDebugEnabled()) {
            log.debug("Evaluated script as...");
            log.debug(text);
        }
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransform() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/transformExample.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformAllInLine() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/transformExampleAllInLine.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformParams() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/transformParamExample.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformParamsInLine() throws Exception {

        final String text = evaluateScriptAsText(testBaseDir + "/transformParamExample2.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSAXOutput() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir + "/transformExampleSAXOutput.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSAXOutputNestedTransforms() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir +
            "/transformExampleSAXOutputNestedTransforms.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testTransformSchematron() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir +
            "/schematron/transformSchematronExample.jelly");
        assertEquals("Should produce the correct output", "Report count=1:assert count=2", text);
    }

    public void testTransformXmlVar() throws Exception {
        final String text = evaluateScriptAsText(testBaseDir +
            "/transformExampleXmlVar.jelly");
        assertEquals("Should produce the correct output", "It works!", text);
    }

    public void testUnitTests() throws Exception {
        runUnitTest( testBaseDir + "/testForEach.jelly" );
    }

}
