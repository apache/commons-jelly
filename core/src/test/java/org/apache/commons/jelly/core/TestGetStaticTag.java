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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.test.BaseJellyTest;

import junit.framework.TestSuite;

/**
 */

public class TestGetStaticTag extends BaseJellyTest {

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestGetStaticTag.class);
    }

    public TestGetStaticTag(final String name) {
        super(name);
    }

    /**
     * Retrieves Integer.MAX_VALUE using tag and verifies against direct
     * access.
     */

    public void testGetIntegerMaxValue() throws Exception {

        setUpScript( "testGetStaticTag.jelly" );

        final Script script = getJelly().compileScript();

        getJellyContext().setVariable( "test.Integer.MAX_VALUE",
                                       Boolean.TRUE );

        script.run( getJellyContext(), getXMLOutput() );

        assertEquals( Integer.valueOf(java.lang.Integer.MAX_VALUE),
                      getJellyContext().getVariable("value" ) );
    }

    /**
     * Retrieves a non-existent field and verifies exception is thrown.
     */

    public void testInvalidGet() throws Exception {

        setUpScript( "testGetStaticTag.jelly" );

        final Script script = getJelly().compileScript();

        getJellyContext().setVariable( "test.InvalidGet", Boolean.TRUE );

        try {
            script.run( getJellyContext(), getXMLOutput() );
        } catch (final JellyTagException jte) {
            return;
        }

        fail("JellyTagException not thrown.");
    }

}

/* Emacs configuration
 * Local variables:        **
 * mode:             java  **
 * c-basic-offset:   4     **
 * indent-tabs-mode: nil   **
 * End:                    **
 */
