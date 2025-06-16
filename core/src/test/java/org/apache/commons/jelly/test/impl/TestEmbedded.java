/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.test.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.Embedded;
import org.xml.sax.InputSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 *  Unit case  of Embedded
 */
public class TestEmbedded extends TestCase
{

    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(TestEmbedded.class);
    }

    public TestEmbedded(final String testName)
    {
        super(testName);
    }

    /**
     *  test Script input as a InputStream
     */
    public void testInputStreamAsScript()
    {
        final Embedded embedded = new Embedded();
        final String jellyScript =
            "<?xml version=\"1.0\"?>"
                + " <j:jelly xmlns:j=\"jelly:core\">"
                + "jelly-test-case"
                + " </j:jelly>";
        embedded.setScript(new ByteArrayInputStream(jellyScript.getBytes()));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        embedded.setOutputStream(baos);
        final boolean status = embedded.execute();
        //executed properly without script errors
        assertEquals(status, true);
        //check that the output confirms the expected
        assertEquals("jelly-test-case", new String(baos.toByteArray()));
    }

    /**
     * Test simple 'raw' execution of a string. See JELLY-189.
     */
    public void testRawExecuteAsString() throws Exception
    {
        final String message =
            "<?xml version=\"1.0\"?>"
                + " <j:jelly xmlns:j=\"jelly:core\">"
                + "jelly-test-case"
                + " </j:jelly>";
       final ByteArrayOutputStream output = new ByteArrayOutputStream();
       final XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
       final InputSource script = new InputSource( new StringReader(message.toString()) );
       final JellyContext context = new JellyContext();
       context.runScript( script, xmlOutput);
       output.close();
       //check that the output confirms the expected
       assertEquals("jelly-test-case", new String(output.toByteArray()));
    }

    /**
     *  test Script input as a java.lang.String object
     */
    public void testStringAsScript()
    {
        final Embedded embedded = new Embedded();
        final String jellyScript =
            "<?xml version=\"1.0\"?>"
                + " <j:jelly xmlns:j=\"jelly:core\">"
                + "jelly-test-case"
                + " </j:jelly>";
        embedded.setScript(jellyScript);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        embedded.setOutputStream(baos);
        boolean status = embedded.execute();
        //executed properly without script errors
        assertTrue("Embedded execution failed", status);
        //check that the output  confirms the expected
        assertEquals("jelly-test-case", new String(baos.toByteArray()));
        //test generation of error
        embedded.setScript(jellyScript + "obnoxious-part");
        status = embedded.execute();
        //test failure of execution
        assertFalse("A script with bad XML was executed successfully", status);
        //Asserting the parser generated a errorMsg
        assertNotNull("A script with bad XML didn't generate an error message", embedded.getErrorMsg());
    }
}
