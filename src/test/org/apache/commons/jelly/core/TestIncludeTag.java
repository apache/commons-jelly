/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/test/org/apache/commons/jelly/core/TestIncludeNesting.java,v 1.3 2002/12/11 12:41:00 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/12/11 12:41:00 $
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
 * $Id: TestIncludeNesting.java,v 1.3 2002/12/11 12:41:00 jstrachan Exp $
 */
package org.apache.commons.jelly.core;

import java.net.URL;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.XMLOutput;

/**
 * Makes sure that nested includes work correctly
 * 
 * @author Morgan Delagrange
 * @version $Revision: 1.3 $
 */
public class TestIncludeTag extends TestCase {

    Jelly jelly = null;
    JellyContext context = null;
    XMLOutput xmlOutput = null;

    public TestIncludeTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestIncludeTag.class);        
    }
    
    public void setUp(String scriptName) throws Exception {
        URL url = this.getClass().getResource(scriptName);
        if ( url == null ) {
            throw new Exception( 
                "Could not find Jelly script: " + scriptName 
                + " in package of class: " + this.getClass().getName() 
            );
        }
        setUpFromURL(url);
    }
    
    public void setUpFromURL(URL url) throws Exception {
        context = new CoreTaglibOnlyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();

        jelly = new Jelly();
        
        jelly.setUrl(url);

        String exturl = url.toExternalForm();
        int lastSlash = exturl.lastIndexOf("/");
        String extBase = exturl.substring(0,lastSlash+1);
        URL baseurl = new URL(extBase);
        context.setCurrentURL(baseurl);
    }
    
    public void testInnermost() throws Exception {
        // performs no includes
        setUp("c.jelly");
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
    }

    public void testMiddle() throws Exception {
        // performs one include
        setUp("b.jelly");
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
    }

    public void testOutermost() throws Exception {
        // performs one nested include
        setUp("a.jelly");
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
        assertTrue("should have set 'a' variable to 'true'",
                   context.getVariable("a").equals("true"));
    }
    
    /**
     * Insure that includes happen correctly when Jelly scripts
     * are referenced as a file (rather than as a classpath
     * element).  Specifically checks to make sure includes succeed
     * when the initial script is not in the user.dir directory.
     */
    public void testFileInclude() throws Exception {
        // testing outermost
        setUpFromURL(new URL("file:src/test/org/apache/commons/jelly/core/a.jelly"));
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
        assertTrue("should have set 'a' variable to 'true'",
                   context.getVariable("a").equals("true"));
    }

    private class CoreTaglibOnlyContext extends JellyContext {

        /**
         * This implementations makes sure that only "jelly:core"
         * taglib is instantiated, insuring that "optional" dependencies
         * are not inadvertantly called.  Specifically addresses a bug
         * in older Jelly dev versions where a nested include
         * would trigger instantiation of all tag libraries.
         * 
         * @param namespaceURI
         * @return 
         */
        public TagLibrary getTagLibrary(String namespaceURI)  {
            if (namespaceURI.equals("jelly:core")) {
                return super.getTagLibrary(namespaceURI);
            } else {
                throw new NoClassDefFoundError("Unexpected tag library uri: " + 
                                                   namespaceURI);
            }
        }

    }

}
