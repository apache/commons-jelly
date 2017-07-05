/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.tags.junit.JellyTestSuite;

/**
 * A helper class to run jelly test cases as part of Ant's JUnit tests
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class TestJelly extends JellyTestSuite {

    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static TestSuite suite() throws Exception {
        return createTestSuite(TestJelly.class, "suite.jelly");
    }
}
