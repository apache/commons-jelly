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
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;

/*
 */
public abstract class BaseJellyTest extends TestCase {

    private Jelly jelly = null;

    private JellyContext context = null;

    private XMLOutput xmlOutput = null;

    public BaseJellyTest(final String name) {
        super(name);
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
    @Override
    public void setUp() throws Exception {
        super.setUp();
        jelly = new Jelly();
        context = new JellyContext();
        xmlOutput = XMLOutput.createDummyXMLOutput();
    }
    protected void setUpScript(final String scriptname) throws Exception {
        final URL url = this.getClass().getResource(scriptname);
        if (null == url) {
            throw new Exception(
                "Could not find Jelly script: " + scriptname
                + " in package of class: " + getClass().getName()
            );
        }
        jelly.setUrl(url);

        final String exturl = url.toExternalForm();
        final int lastSlash = exturl.lastIndexOf("/");
        final String extBase = exturl.substring(0,lastSlash+1);
        final URL baseurl = new URL(extBase);
        context.setCurrentURL(baseurl);
    }

}
