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
package org.apache.commons.jelly.core;

import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Makes sure that nested includes work correctly
 */
public class TestIncludeTag extends TestCase {

    private final class CoreTaglibOnlyContext extends JellyContext {

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
        @Override
        public TagLibrary getTagLibrary(final String namespaceURI)  {
            if (namespaceURI.equals("jelly:core")) {
                return super.getTagLibrary(namespaceURI);
            }
            throw new NoClassDefFoundError("Unexpected tag library uri: " +
                                               namespaceURI);
        }

    }
    public static TestSuite suite() throws Exception {
        return new TestSuite(TestIncludeTag.class);
    }
    Jelly jelly = null;

    JellyContext context = null;

    XMLOutput xmlOutput = null;

    public TestIncludeTag(final String name) {
        super(name);
    }

    public void setUp(final String scriptName) throws Exception {
        final URL url = this.getClass().getResource(scriptName);
        if ( url == null ) {
            throw new Exception(
                "Could not find Jelly script: " + scriptName
                + " in package of class: " + this.getClass().getName()
            );
        }
        setUpFromURL(url);
    }

    public void setUpFromURL(final URL url) throws Exception {
        context = new CoreTaglibOnlyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();

        jelly = new Jelly();

        jelly.setUrl(url);

        final String exturl = url.toExternalForm();
        final int lastSlash = exturl.lastIndexOf("/");
        final String extBase = exturl.substring(0,lastSlash+1);
        final URL baseurl = new URL(extBase);
        context.setCurrentURL(baseurl);
    }

    /**
     * Insure that includes happen correctly when Jelly scripts
     * are referenced as a file (rather than as a classpath
     * element).  Specifically checks to make sure includes succeed
     * when the initial script is not in the user.dir directory.
     */
    public void testFileInclude() throws Exception {
        // testing outermost
        setUpFromURL(new URL("file:src/test/resources/org/apache/commons/jelly/core/a.jelly"));
        final Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
        assertTrue("should have set 'a' variable to 'true'",
                   context.getVariable("a").equals("true"));
    }

    public void testInnermost() throws Exception {
        // performs no includes
        setUp("c.jelly");
        final Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
    }

    public void testMiddle() throws Exception {
        // performs one include
        setUp("b.jelly");
        final Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
    }

    public void testOutermost() throws Exception {
        // performs one nested include
        setUp("a.jelly");
        final Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'c' variable to 'true'",
                   context.getVariable("c").equals("true"));
        assertTrue("should have set 'b' variable to 'true'",
                   context.getVariable("b").equals("true"));
        assertTrue("should have set 'a' variable to 'true'",
                   context.getVariable("a").equals("true"));
    }

}
