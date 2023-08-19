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
/*
 * Created on 18 nov. 2004
 */
package org.apache.commons.jelly.core;

import org.apache.commons.jelly.tags.junit.AbstractJellyTestSuite;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * New Catch improvement test suite
 * @author mde
 * @version 0.0
 */
public class TestCatchTag extends AbstractJellyTestSuite {
    
    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static TestSuite suite() throws Exception {
        TestSuite suite = createTestSuite(TestCatchTag.class, "testNewCatchTag.xml");
        suite.addTest(new TestModularExceptionBean("testThrowIt"));
        return suite;
    }
    


}
