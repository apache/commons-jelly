/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/core/TestInvokeStaticTag.java,v 1.2 2003/02/26 09:12:55 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2003/02/26 09:12:55 $
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
 * $Id: TestInvokeStaticTag.java,v 1.2 2003/02/26 09:12:55 jstrachan Exp $
 */
package org.apache.commons.jelly.core;

import junit.framework.TestSuite;

import org.apache.commons.jelly.Script;

/**
 * @author <a href="mailto:robert@bull-enterprises.com">Robert McIntosh</a>
 * @version $Revision: 1.2 $
 */
public class TestInvokeStaticTag extends BaseJellyTest {

    public TestInvokeStaticTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestInvokeStaticTag.class);        
    }

    public void setUp() throws Exception {
        super.setUp();
    }    
    
    public void tearDown() throws Exception {
        super.tearDown();
    }  
    
    /**
     *  Gets the System property 'java.runtime.version' and compares it with,
     *  well, the same system property
     */
     public void testSimpleSystemInvoke() throws Exception {
        setUpScript( "testInvokeStaticTag.jelly" );
        Script script = getJelly().compileScript();
        
        getJellyContext().setVariable( "test.simpleSystemInvoke",Boolean.TRUE );
        
        getJellyContext().setVariable( "propertyName", "java.runtime.version" );
        script.run( getJellyContext(),getXMLOutput() );
                
        assertTrue( System.getProperty( "java.runtime.version" ).equals( getJellyContext().getVariable("propertyName" ) ) );        
    }

     /**
     *  Sets the System property 'TEST PROPERTY' to the value 'Jelly is cool' and compares it with,
     *  well, the same system property
     */
    public void testSystemInvoke() throws Exception {
        setUpScript( "testInvokeStaticTag.jelly" );
        Script script = getJelly().compileScript();
        
        getJellyContext().setVariable( "test.systemInvoke",Boolean.TRUE );
        
        getJellyContext().setVariable( "propertyName", "TEST PROPERTY" );
        getJellyContext().setVariable( "propertyValue", "Jelly is cool" );
        script.run( getJellyContext(),getXMLOutput() );
                
        assertTrue( System.getProperty( "TEST PROPERTY" ).equals( "Jelly is cool" ) );
        
    }

     /**
     *  Uses the java.text.MessageFormat class to format a text message
     *  with 3 arguments. 
     */
    public void testMessageFormatInvoke() throws Exception {
        System.out.println( System.getProperties() );
        setUpScript( "testInvokeStaticTag.jelly" );
        Script script = getJelly().compileScript();
        
        getJellyContext().setVariable( "test.messageFormatInvoke", Boolean.TRUE );
        
        Object[] args = new Object[3];
        args[0] = "Jelly";
        args[1] = "coolest";
        args[2] = "used";
        
        getJellyContext().setVariable( "args", args );
        getJellyContext().setVariable( "message", "Is not {0} the {1} thing you have ever {2}?" );
        script.run( getJellyContext(),getXMLOutput() );
         
        assertNotNull( getJellyContext().getVariable("message") );
        assertTrue( getJellyContext().getVariable("message").equals("Is not Jelly the coolest thing you have ever used?") );
        
    }
}
