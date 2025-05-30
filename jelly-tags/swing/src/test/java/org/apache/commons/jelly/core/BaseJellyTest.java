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

import junit.framework.TestCase;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

/*
 */
public abstract class BaseJellyTest extends TestCase {

    public BaseJellyTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        jelly = new Jelly();
        context = new JellyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();
    }

    protected void setUpScript(String scriptname) throws Exception {
        URL url = this.getClass().getResource(scriptname);
        if (null == url) {
            throw new Exception(
                "Could not find Jelly script: " + scriptname
                + " in package of class: " + getClass().getName()
            );
        }
        jelly.setUrl(url);

        String exturl = url.toExternalForm();
        int lastSlash = exturl.lastIndexOf("/");
        String extBase = exturl.substring(0,lastSlash+1);
        URL baseurl = new URL(extBase);
        context.setCurrentURL(baseurl);
    }

    protected Jelly getJelly() {
        return jelly;
    }

    protected JellyContext getJellyContext() {
        return context;
    }

    protected XMLOutput getXMLOutput() {
        return xmlOutput;
    }

    private Jelly jelly = null;
    private JellyContext context = null;
    private XMLOutput xmlOutput = null;

}
