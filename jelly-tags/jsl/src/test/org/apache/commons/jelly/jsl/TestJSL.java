/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/test/org/apache/commons/jelly/TestCoreTags.java,v 1.8 2002/05/28 07:20:06 jstrachan Exp $
 * $Revision: 1.8 $
 * $Date: 2002/05/28 07:20:06 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: TestCoreTags.java,v 1.8 2002/05/28 07:20:06 jstrachan Exp $
 */
package org.apache.commons.jelly.jsl;

import java.io.FileInputStream;
import java.io.InputStream;

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
import org.dom4j.Element;
import org.dom4j.io.SAXContentHandler;

/** 
 * Tests the JSL tags.
 * Note this test harness could be written in Jelly script
 * if we had the junit tag library!
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class TestJSL extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestJSL.class);

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestJSL.class);
    }

    public TestJSL(String testName) {
        super(testName);
    }

    public void testExample1() throws Exception {
        Document document = runScript( "src/test/org/apache/commons/jelly/jsl/example.jelly" );
        Element small = (Element) document.selectSingleNode("/html/body/small");
        
        assertTrue( "<small> starts with 'James Elson'", small.getText().startsWith("James Elson") );
        assertEquals( "I am a title!", small.valueOf( "h2" ).trim() );
        assertEquals( "Twas a dark, rainy night...", small.valueOf( "small" ).trim() );
        assertEquals( "dfjsdfjsdf", small.valueOf( "p" ).trim() );
    }
    
        
    protected Document runScript(String fileName) throws Exception {
        InputStream in = new FileInputStream(fileName);
        XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();
        JellyContext context = parser.getContext();
        
        SAXContentHandler contentHandler = new SAXContentHandler();
        XMLOutput output = new XMLOutput( contentHandler );
        
        contentHandler.startDocument();
        script.run(context, output);
        contentHandler.endDocument();
        
        return contentHandler.getDocument();
    }
}
