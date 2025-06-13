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
package org.apache.commons.jelly.tags.beanshell;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Tests the BeanShell EL
  */
public class TestBeanShellEL extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( TestBeanShellEL.class );

    public static void main( final String[] args ) {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestBeanShellEL.class);
    }

    /** Jelly context */
    protected JellyContext context;

    /** The factory of Expression objects */
    protected ExpressionFactory factory;

    public TestBeanShellEL(final String testName) {
        super(testName);
    }

    /** Evaluates the given expression text and tests it against the expected value */
    protected void assertExpression( final String expressionText, final Object expectedValue ) throws Exception {
        final Expression expr = factory.createExpression( expressionText );
        final Object value = expr.evaluate( context );
        assertEquals( "Value of expression: " + expressionText, expectedValue, value );
    }

    @Override
    public void setUp() {
        context = new JellyContext();
        context.setVariable( "foo", "abc" );
        context.setVariable( "bar", new Integer( 123 ) );
        factory = new BeanShellExpressionFactory();
    }

    public void testEL() throws Exception {
        assertExpression( "foo", "abc" );
        assertExpression( "bar * 2", new Integer( 246 ) );
        assertExpression( "bar == 123", Boolean.TRUE );
        assertExpression( "bar == 124", Boolean.FALSE );
        assertExpression( "foo.equals( \"abc\" )", Boolean.TRUE );
        assertExpression( "foo.equals( \"xyz\" )", Boolean.FALSE );
    }
}

