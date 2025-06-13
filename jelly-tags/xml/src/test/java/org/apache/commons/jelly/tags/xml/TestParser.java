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
package org.apache.commons.jelly.tags.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests the core tags
  */
public class TestParser extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestParser.class);

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestParser.class);
    }

    public TestParser(final String testName) {
        super(testName);
    }

    /**
     * Tests that the Tag in the TagScript has the given parent and then
     * recurse to check its children has the correct parent and so forth.
     */
    protected void assertTagsHaveParent(final Script script, final Tag parent, JellyContext context) throws Exception {
        if ( context == null ) {
            context = new JellyContext();
        }
        if ( script instanceof TagScript ) {
            final TagScript tagScript = (TagScript) script;
            final Tag tag = tagScript.getTag(context);

            assertEquals( "Tag: " + tag + " has the incorrect parent", parent, tag.getParent() );

            assertTagsHaveParent( tag.getBody(), tag, context );
        }
        else if ( script instanceof ScriptBlock ) {
            final ScriptBlock block = (ScriptBlock) script;
            for ( final Iterator iter = block.getScriptList().iterator(); iter.hasNext(); ) {
                assertTagsHaveParent( (Script) iter.next(), parent, context );
            }
        }
    }

    /**
     * Tests that parsing an example script correctly creates the parent
     * relationships
     */
    public void testParser() throws Exception {
        final InputStream in = new FileInputStream("target/test-classes/org/apache/commons/jelly/tags/xml/example2.jelly");
        final XMLParser parser = new XMLParser();
        Script script = parser.parse(in);
        script = script.compile();

        log.debug("Found: " + script);

        assertTagsHaveParent( script, null, null );
    }
}