/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/tags/junit/JellyTestSuite.java,v 1.2 2003/10/09 21:21:31 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/09 21:21:31 $
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
 * $Id: JellyTestSuite.java,v 1.2 2003/10/09 21:21:31 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.junit;

import java.net.URL;

import junit.framework.TestSuite;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * An abstract base class for creating a TestSuite via a Jelly script.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public abstract class JellyTestSuite {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JellyTestSuite.class);


    /**
     * Helper method to create a test suite from a file name on the class path
     * in the package of the given class. 
     * For example a test could call 
     * <code>
     * createTestSuite( Foo.class, "suite.jelly" );
     * </code>
     * which would loaad the 'suite.jelly script from the same package as the Foo 
     * class on the classpath.
     * 
     * @param testClass is the test class used to load the script via the classpath
     * @param script is the name of the script, which is typically just a name, no directory.
     * @return a newly created TestSuite
     */
    public static TestSuite createTestSuite(Class testClass, String script) throws Exception {
        URL url = testClass.getResource(script);
        if ( url == null ) {
            throw new Exception( 
                "Could not find Jelly script: " + script 
                + " in package of class: " + testClass.getName() 
            );
        }
        return createTestSuite( url );
    }
        
    /**
     * Helper method to create a test suite from the given Jelly script
     * 
     * @param script is the URL to the script which should create a TestSuite
     * @return a newly created TestSuite
     */
    public static TestSuite createTestSuite(URL script) throws Exception {
        JellyContext context = new JellyContext(script);
        XMLOutput output = XMLOutput.createXMLOutput(System.out);
        context = context.runScript(script, output);
        TestSuite answer = (TestSuite) context.getVariable("org.apache.commons.jelly.junit.suite");
        if ( answer == null ) {
            log.warn( "Could not find a TestSuite created by Jelly for the script:" + script );
            // return an empty test suite
            return new TestSuite();
        }
        return answer;
    }
}
