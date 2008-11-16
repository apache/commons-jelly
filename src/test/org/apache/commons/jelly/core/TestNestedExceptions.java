package org.apache.commons.jelly.core;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.tags.junit.JellyTestSuite;
import org.apache.commons.jelly.TestJelly;
import org.apache.commons.jelly.test.BaseJellyTest;

public class TestNestedExceptions extends JellyTestSuite {

    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static TestSuite suite() throws Exception {
        return createTestSuite(TestNestedExceptions.class, "testNestedExceptions.xml");
    }
    
}
