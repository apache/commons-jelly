/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/CaseTag.java,v 1.8 2002/07/06 13:53:39 dion Exp $
 * $Revision: 1.8 $
 * $Date: 2002/07/06 13:53:39 $
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
 * $Id: CaseTag.java,v 1.8 2002/07/06 13:53:39 dion Exp $
 */
package org.apache.commons.jelly.tags.junit;

import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/** 
 * Represents a single test case in a test suite; this tag is analagous to
 * JUnit's TestCase class.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class CaseTag extends TagSupport {

    private String name;
    
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(final XMLOutput output) throws Exception {
        String name = getName();
        if ( name == null ) {
            name = toString();
        }
        
        // #### we need to redirect the output to a TestListener
        // or something?
        TestCase testCase = new TestCase(name) {
            protected void runTest() throws Throwable {
                invokeBody(output);
            }
        };
        
        // lets find the test suite
        TestSuite suite = getSuite();
        if ( suite == null ) {
            throw new JellyException( "Could not find a TestSuite to add this test to. This tag should be inside a <test:suite> tag" );
        }
        suite.addTest(testCase);
    }
    
    // Properties
    //-------------------------------------------------------------------------                

    /**
     * @return the name of this test case
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the name of this test case
     */
    public void setName(String name) {
        this.name = name;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /**
     * Strategy method to find the corrent TestSuite to add a new Test case to
     */
    protected TestSuite getSuite() {
        SuiteTag tag = (SuiteTag) findAncestorWithClass( SuiteTag.class );
        if ( tag != null ) {
            return tag.getSuite();
        }
        return (TestSuite) context.getVariable( "org.apache.commons.jelly.junit.suite" );
    }

}
