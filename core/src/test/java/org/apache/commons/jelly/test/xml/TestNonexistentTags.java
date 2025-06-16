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

package org.apache.commons.jelly.test.xml;

import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A test to confirm that Jelly scripts fail to parse if they declare tags
 * that do not exist
 */
public class TestNonexistentTags extends TestCase {
     public static TestSuite suite() throws Exception {
        return new TestSuite(TestNonexistentTags.class);
    }
    Jelly jelly = null;
    JellyContext context = null;

    XMLOutput xmlOutput = null;

    public TestNonexistentTags(final String name) {
        super(name);
    }

    public void setUp(final String scriptName) throws Exception {
        context = new JellyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();

        jelly = new Jelly();

        final String script = scriptName;
        final URL url = this.getClass().getResource(script);
        if ( url == null ) {
            throw new Exception(
                "Could not find Jelly script: " + script
                + " in package of class: " + this.getClass().getName()
            );
        }
        jelly.setUrl(url);
    }

    /**
     * A script should fail to parse if it declares tags that don't exist.
     */
    public void testNonexistentTags() throws Exception {
        setUp("nonexistentTags1.jelly");
        try {
            jelly.compileScript();
            fail("Scripts should throw JellyException when it declares a nonexistent tag.");
        } catch (final JellyException e) {
        }
    }

}
