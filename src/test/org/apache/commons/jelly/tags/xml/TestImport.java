/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.xml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;

/**
 * Test that compiled scripts can access resources
 */
public class TestImport extends TestCase {
    
    public TestImport(String name) {
        super(name);
    }
    
    public void testImportResources() throws JellyException, UnsupportedEncodingException, IOException {
        JellyContext context = new JellyContext();
        URL url = TestImport.class.getResource("/resources/import.jelly");
        XMLOutput out = XMLOutput.createXMLOutput(System.out);
//         this works because of the created child context that has knowledge
//         of the URL
        context.runScript(url, out);
        out.close();
    }

    public void testImportResourcesCompiled() throws JellyException, UnsupportedEncodingException, IOException {
        JellyContext context = new JellyContext();
        URL url = TestImport.class.getResource("/resources/import.jelly");
        XMLOutput out = XMLOutput.createXMLOutput(System.out);
//      This does not work because context has no currentURL set
//      This results in a NullPointerException when resolving the
//      stylesheet
//        Script script = context.compileScript(url);
//        script.run(context, out);
//        out.close();
    }

}