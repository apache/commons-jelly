/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/tags/junit/RunTag.java,v 1.2 2003/01/27 02:35:26 dion Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/27 02:35:26 $
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
 * $Id: RunTag.java,v 1.2 2003/01/27 02:35:26 dion Exp $
 */
package org.apache.commons.jelly.tags.junit;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/** 
 * This tag will run the given Test which could be an individual TestCase or a TestSuite.
 * The TestResult can be specified to capture the output, otherwise the results are output
 * as XML so that they can be formatted in some custom manner.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class RunTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(RunTag.class);
    
    private Test test;
    private TestResult result;
    private TestListener listener;
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws JellyTagException {
        Test test = getTest();
        if ( test == null ) {
            test = (Test) context.getVariable("org.apache.commons.jelly.junit.suite");        
        }
        if ( test == null ) {
            throw new MissingAttributeException( "test" );
        }
        TestResult result = getResult();
        if ( result == null ) {
            result = createResult(output);                    
        }
        TestListener listener = getListener();
        if ( listener == null ) {
            listener = createTestListener(output);
        }
        result.addListener(listener);
        test.run(result);
    }
    
    // Properties
    //-------------------------------------------------------------------------                

    /**
     * Returns the TestResult used to capture the output of the test.
     * @return TestResult
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Returns the Test to be ran.
     * @return Test
     */
    public Test getTest() {
        return test;
    }

    /**
     * Sets the JUnit TestResult used to capture the results of the tst
     * @param result The TestResult to use
     */
    public void setResult(TestResult result) {
        this.result = result;
    }

    /**
     * Sets the JUnit Test to run which could be an individual test or a TestSuite
     * @param test The test to run
     */
    public void setTest(Test test) {
        this.test = test;
    }

    /**
     * Returns the listener.
     * @return TestListener
     */
    public TestListener getListener() {
        return listener;
    }

    /**
     * Sets the TestListener.to be used to format the output of running the unit test cases
     * @param listener The listener to set
     */
    public void setListener(TestListener listener) {
        this.listener = listener;
    }



    // Implementation methods
    //-------------------------------------------------------------------------                

    /**
     * Factory method to create a new TestResult to capture the output of
     * the test cases
     */
    protected TestResult createResult(XMLOutput output) {
        return new TestResult();
    }
    
    /**
     * Factory method to create a new TestListener to capture the output of
     * the test cases
     */    
    protected TestListener createTestListener(final XMLOutput output) {
        return new TestListener() {
            public void addError(Test test, Throwable t) {
                try {
                    output.startElement("error");
    
                    output.startElement("message");
                    output.write(t.getMessage());
                    output.endElement("message");
    
                    output.startElement("stack");
                    output.write( stackTraceToString(t) );
                    output.endElement("stack");
    
                    output.endElement("error");
                }
                catch (SAXException e) {
                    handleSAXException(e);
                }
            }
            
            public void addFailure(Test test, AssertionFailedError t) {
                try {
                    output.startElement("failure");
    
                    output.startElement("message");
                    output.write(t.getMessage());
                    output.endElement("message");
    
                    output.startElement("stack");
                    output.write( stackTraceToString(t) );
                    output.endElement("stack");
    
                    output.endElement("failure");
                }
                catch (SAXException e) {
                    handleSAXException(e);
                }
            }
            
            public void endTest(Test test) {
                try {
                    output.endElement("test");
                }
                catch (SAXException e) {
                    handleSAXException(e);
                }
            }
            
            public void startTest(Test test) {
                try {
                    String name = test.toString();
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "name", "name", "CDATA", name);
                    
                    output.startElement("test", attributes);
                }
                catch (SAXException e) {
                    handleSAXException(e);
                }
            }
        };
    }

    /**
     * @return the stack trace as a String
     */
    protected String stackTraceToString(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
    
    /**
     * Handles SAX Exceptions
     */
    protected void handleSAXException(SAXException e) {
        log.error( "Caught: " + e, e );
    }
}
