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
package org.apache.commons.jelly;

import java.net.URL;

import junit.framework.TestCase;

/**
 * A test class to validate doctype definitions' declaration of external
 * calls using custom XML tags. Specifically we test some changes in {@link JellyContext}
 * along with {@link org.apache.commons.jelly.parser.XMLParser}.
 */
public class TestDoctypeDefinitionXXE extends TestCase
{
    public TestDoctypeDefinitionXXE( final String s )
    {
        super( s );
    }

    public void testDoctypeDefinitionXXEAllowDTDCalls() throws JellyException
    {
        final JellyContext context = new JellyContext();
        context.setAllowDtdToCallExternalEntities(true);
        final URL url = this.getClass().getResource("doctypeDefinitionXXE.jelly");
        try
        {
            context.runScript(url, null);
        } catch (final JellyException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof java.net.ConnectException) {
                //success
            } else if (cause instanceof org.xml.sax.SAXParseException) {
                fail("doctypeDefinitionXXE.jelly did not attempt to connect to http://127.0.0.1:4444");
            } else {
                fail("Unknown exception: " + e.getMessage());
            }
        }
    }

    public void testDoctypeDefinitionXXEDefaultMode() throws JellyException
    {
        final JellyContext context = new JellyContext();
        final URL url = this.getClass().getResource("doctypeDefinitionXXE.jelly");
        try
        {
            context.runScript(url, null);
        } catch (final JellyException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof java.net.ConnectException) {
                fail("doctypeDefinitionXXE.jelly attempted to connect to http://127.0.0.1:4444");
            } else if (cause instanceof org.xml.sax.SAXParseException) {
                // Success.
            } else {
                fail("Unknown exception: " + e.getMessage());
            }
        }
    }
}