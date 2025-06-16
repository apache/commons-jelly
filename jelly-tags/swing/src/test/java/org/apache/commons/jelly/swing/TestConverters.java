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
package org.apache.commons.jelly.swing;

import java.awt.Dimension;
import java.awt.Point;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.jelly.tags.swing.SwingTagLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests the Swing converters
 */
public class TestConverters extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestConverters.class);

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestConverters.class);
    }

    /** Delta used to compare doubles */
    double delta = 0.0000001;

    // force the Swing converters to be loaded
    SwingTagLibrary dummy = new SwingTagLibrary();

    public TestConverters(final String testName) {
        super(testName);
    }

    protected void assertDimension(final String expression, final Dimension expected) throws Exception {
        final Object answer = ConvertUtils.convert(expression, Dimension.class );

        assertTrue( "Returned type: "+  answer.getClass() + " is-a Dimension", answer instanceof Dimension );

        final Dimension value = (Dimension) answer;

        assertEquals( "width", expected.getWidth(), value.getWidth(), delta );
        assertEquals( "height", expected.getHeight(), value.getHeight(), delta );

        assertEquals( expected, value );
    }

    protected void assertPoint(final String expression, final Point expected) throws Exception {
        final Object answer = ConvertUtils.convert(expression, Point.class );

        assertTrue( "Returned type: "+  answer.getClass() + " is-a Point", answer instanceof Point );

        final Point value = (Point) answer;

        assertEquals( "x", expected.getX(), value.getX(), delta );
        assertEquals( "y", expected.getY(), value.getY(), delta );

        assertEquals( expected, value );
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    public void testDimensions() throws Exception {
        assertDimension("100, 200", new Dimension(100, 200));
        assertDimension("100", new Dimension(100, 0));
        assertDimension(" 100  ,  200  ", new Dimension(100, 200));
        assertDimension(" 0  ,  200  ", new Dimension(0, 200));
    }

    public void testPoints() throws Exception {
        assertPoint("100, 200", new Point(100, 200));
        assertPoint("100", new Point(100, 0));
        assertPoint(" 100  ,  200  ", new Point(100, 200));
        assertPoint(" 0  ,  200  ", new Point(0, 200));
    }
}
