/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/test/org/apache/commons/jelly/tags/define/TestDynamicTags.java,v 1.2 2003/10/09 21:21:17 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/09 21:21:17 $
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
 * $Id: TestDynamicTags.java,v 1.2 2003/10/09 21:21:17 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.define;

import java.io.File;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Tests dynamic tags
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.2 $
  */
public class TestDynamicTags extends TestCase {

    JellyContext context = new JellyContext();
    XMLOutput output;

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestDynamicTags.class);

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestDynamicTags.class);
    }

    public TestDynamicTags(String testName) {
        super(testName);
    }

    public void testParse() throws Exception {
        StringWriter buffer = new StringWriter();        
        output = XMLOutput.createXMLOutput(buffer);
        
        //runScript("src/test/org/apache/commons/jelly/define/babelfishTaglib.jelly");
        runScript("src/test/org/apache/commons/jelly/tags/define/example.jelly");
        
        log.info("The output was as follows");
        log.info(buffer.toString());
    }
    
    public void testJellyBean() throws Exception {
        StringWriter buffer = new StringWriter();        
        output = XMLOutput.createXMLOutput(buffer);
        
	log.warn("commented out test, need to rewrite without ant");
        //runScript("src/test/org/apache/commons/jelly/define/jellyBeanSample.jelly");
        
        log.info("The output was as follows");
        log.info(buffer.toString());
    }
    
    protected void runScript(String name) throws Exception {
        context.runScript(new File(name), output);
    }
}
