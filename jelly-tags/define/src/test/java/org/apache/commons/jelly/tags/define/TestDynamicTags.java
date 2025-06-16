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
package org.apache.commons.jelly.tags.define;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests dynamic tags
  */
public class TestDynamicTags extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestDynamicTags.class);
    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestDynamicTags.class);
    }

    JellyContext context = new JellyContext();

    XMLOutput output;

    public TestDynamicTags(final String testName) {
        super(testName);
    }

    protected void runScript(final String name) throws Exception {
        context.runScript(new File(name), output);
    }

    public void testJellyBean() throws Exception {
        final StringWriter buffer = new StringWriter();
        output = XMLOutput.createXMLOutput(buffer);

    log.warn("commented out test, need to rewrite without ant");
        //runScript("src/test/org/apache/commons/jelly/define/jellyBeanSample.jelly");

        log.info("The output was as follows");
        log.info(buffer.toString());
    }

    public void testParse() throws Exception {
        final StringWriter buffer = new StringWriter();
        output = XMLOutput.createXMLOutput(buffer);

        //runScript("src/test/org/apache/commons/jelly/define/babelfishTaglib.jelly");
        runScript("target/test-classes/org/apache/commons/jelly/tags/define/example.jelly");

        log.info("The output was as follows");
        log.info(buffer.toString());
    }
}
