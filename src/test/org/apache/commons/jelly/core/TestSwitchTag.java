/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/core/TestSwitchTag.java,v 1.3 2002/10/22 16:15:49 rwaldhoff Exp $
 * $Revision: 1.3 $
 * $Date: 2002/10/22 16:15:49 $
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
 * $Id: TestSwitchTag.java,v 1.3 2002/10/22 16:15:49 rwaldhoff Exp $
 */
package org.apache.commons.jelly.core;

import java.net.URL;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * @author Rodney Waldhoff
 * @version $Revision: 1.3 $ $Date: 2002/10/22 16:15:49 $
 */
public class TestSwitchTag extends TestCase {

    public TestSwitchTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestSwitchTag.class);        
    }

    public void setUp() throws Exception {
        super.setUp();
        jelly = new Jelly();
        context = new JellyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();        
    }    
    
    private void setUpScript(String scriptname) throws Exception {
        URL url = this.getClass().getResource(scriptname);
        if(null == url) {
            throw new Exception( 
                "Could not find Jelly script: " + scriptname 
                + " in package of class: " + getClass().getName() 
            );
        }
        jelly.setUrl(url);

        String exturl = url.toExternalForm();
        int lastSlash = exturl.lastIndexOf("/");
        String extBase = exturl.substring(0,lastSlash+1);
        URL baseurl = new URL(extBase);
        context.setCurrentURL(baseurl);
    }
    
    public void testSimpleSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("switch.on.a","two");
        script.run(context,xmlOutput);
        assertNull("should not have 'a.one' variable set",
                   context.getVariable("a.one"));
        assertTrue("should have set 'a.two' variable to 'true'",
                   context.getVariable("a.two").equals("true"));
        assertNull("should not have 'a.three' variable set",
                   context.getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   context.getVariable("a.null"));
        assertNull("should not have 'a.default' variable set",
                   context.getVariable("a.default"));
    }

    public void testFallThru() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("switch.on.a","one");
        script.run(context,xmlOutput);
        assertTrue("should have set 'a.one' variable to 'true'",
                   context.getVariable("a.one").equals("true"));
        assertTrue("should have set 'a.two' variable to 'true'",
                   context.getVariable("a.two").equals("true"));
        assertNull("should not have 'a.three' variable set",
                   context.getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   context.getVariable("a.null"));
        assertNull("should not have 'a.default' variable set",
                   context.getVariable("a.default"));
    }

    public void testDefault() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("switch.on.a","negative one");
        script.run(context,xmlOutput);
        assertNull("should not have 'a.one' variable set",
                   context.getVariable("a.one"));
        assertNull("should not have 'a.two' variable set",
                   context.getVariable("a.two"));
        assertNull("should not have 'a.three' variable set",
                   context.getVariable("a.three"));
        assertNull("should not have 'a.null' variable set",
                   context.getVariable("a.null"));
        assertTrue("should have set 'a.default' variable to 'true'",
                   context.getVariable("a.default").equals("true"));
    }

    public void testNullCase() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("switch.on.a",null);
        script.run(context,xmlOutput);
        assertNull("should not have 'a.one' variable set",
                   context.getVariable("a.one"));
        assertNull("should not have 'a.two' variable set",
                   context.getVariable("a.two"));
        assertNull("should not have 'a.three' variable set",
                   context.getVariable("a.three"));
        assertTrue("should have set 'a.null' variable to 'true'",
                   context.getVariable("a.null").equals("true"));
        assertNull("should not have 'a.default' variable set",
                   context.getVariable("a.default"));
    }

    public void testSwitchWithoutOn() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("switch.without.on",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected MissingAttributeException");
        } catch(MissingAttributeException e) {
            // expected
        }
    }

    public void testCaseWithoutSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("case.without.switch",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected JellyException");
        } catch(JellyException e) {
            // expected
        }
    }

    public void testDefaultWithoutSwitch() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("default.without.switch",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected JellyException");
        } catch(JellyException e) {
            // expected
        }
    }
    
    public void testCaseWithoutValue() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("case.without.value",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected MissingAttributeException");
        } catch(MissingAttributeException e) {
            // expected
        }
    }
    
    public void testMultipleDefaults() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("multiple.defaults",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected JellyException");
        } catch(JellyException e) {
            // expected
        }
    }
    
    public void testCaseAfterDefault() throws Exception {
        setUpScript("testSwitchTag.jelly");
        Script script = jelly.compileScript();
        context.setVariable("case.after.default",new Boolean(true));
        try {
            script.run(context,xmlOutput);
            fail("Expected JellyException");
        } catch(JellyException e) {
            // expected
        }
    }
    
    private Jelly jelly = null;
    private JellyContext context = null;
    private XMLOutput xmlOutput = null;

}
