/*
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
 */
package org.apache.commons.jelly.test.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.jelly.impl.Embedded;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 *  Unit case  of Embedded 
 * 
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class TestEmbedded extends TestCase
{

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(TestEmbedded.class);
    }

    public TestEmbedded(String testName)
    {
        super(testName);
    }

    /**
     *  test Script input as a java.lang.String object
     */
    public void testStringAsScript()
    {
        Embedded embedded = new Embedded();
        String jellyScript =
            "<?xml version=\"1.0\"?>"
                + " <j:jelly xmlns:j=\"jelly:core\">"
                + "jelly-test-case"
                + " </j:jelly>";
        embedded.setScript(jellyScript);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        embedded.setOutputStream(baos);
        boolean status = embedded.execute();
        //executed properly without script errors
        assertEquals(status, true);
        //check that the output  confirms the exepected
        assertEquals("jelly-test-case", new String(baos.toByteArray()));
        //test generation of error
        embedded.setScript(jellyScript + "obnoxious-part");
        status = embedded.execute();
        //test failure of execution
        assertEquals(false, status);
        //Asserting the parser generated a errorMsg
        assertNotNull(embedded.getErrorMsg());
    }

    /**
     *  test Script input as a InputStream
     */
    public void testInputStreamAsScript()
    {
        Embedded embedded = new Embedded();
        String jellyScript =
            "<?xml version=\"1.0\"?>"
                + " <j:jelly xmlns:j=\"jelly:core\">"
                + "jelly-test-case"
                + " </j:jelly>";
        embedded.setScript(new ByteArrayInputStream(jellyScript.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        embedded.setOutputStream(baos);
        boolean status = embedded.execute();
        //executed properly without script errors
        assertEquals(status, true);
        //check that the output  confirms the exepected
        assertEquals("jelly-test-case", new String(baos.toByteArray()));

    }
}
