/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/TestCoreTags.java,v 1.2 2002/02/19 15:40:58 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/19 15:40:58 $
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
 * $Id: TestCoreTags.java,v 1.2 2002/02/19 15:40:58 jstrachan Exp $
 */
package org.apache.commons.jelly;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.Context;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.parser.XMLParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** Tests the core tags
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.2 $
  */
public class TestCoreTags extends TestCase {
    
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( TestXMLTags.class );

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestCoreTags.class);
    }
    
    public TestCoreTags(String testName) {
        super(testName);
    }
    
    public void testArgs() throws Exception {
        InputStream in = getClass().getResourceAsStream( "testing123.jelly" );
        XMLParser parser = new XMLParser();
        Script script = parser.parse( in );
        script = script.compile();

        log.debug( "Found: " + script );
        
        assertTrue( "Script is a TagScript", script instanceof TagScript );
        
        String[] args = { "one", "two", "three" };
        Context context = new Context();        
        context.setVariable( "args", args );
        StringWriter buffer = new StringWriter();
        
        script.run( context, buffer );
        
        String text = buffer.toString().trim();
        
        if ( log.isDebugEnabled() ) {
            log.debug( "Evaluated script as..." );
            log.debug( text );
        }
        
        assertEquals( "Produces the correct output", "one two three", text );        
    }    
}

