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
package org.apache.commons.jelly.expression;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils2.BasicDynaClass;
import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaClass;
import org.apache.commons.beanutils2.DynaProperty;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests the use of Expression parsing
 */
public class TestDynaBeans extends TestCase {

    public static void main(final String[] args) {
        TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(TestDynaBeans.class);
    }

    protected JellyContext context = new JellyContext();

    protected ExpressionFactory factory = new JexlExpressionFactory();

    public TestDynaBeans(final String testName) {
        super(testName);
    }

    protected void assertExpression(final String expressionText, final Object expectedValue) throws Exception {
        final Expression expression = CompositeExpression.parse(expressionText, factory);
        assertTrue( "Created a valid expression for: " + expressionText, expression != null );
        final Object value = expression.evaluate(context);
        //assertEquals( "Expression for: " + expressionText + " is: " + expression, expectedValue, value );
        assertEquals( "Wrong result for expression: " + expressionText, expectedValue, value );
    }

    protected DynaClass createDynaClass() {
        final DynaProperty[] properties = {
            new DynaProperty("booleanProperty", Boolean.TYPE),
            new DynaProperty("booleanSecond", Boolean.TYPE),
            new DynaProperty("doubleProperty", Double.TYPE),
            new DynaProperty("floatProperty", Float.TYPE),
            new DynaProperty("intProperty", Integer.TYPE),
            new DynaProperty("listIndexed", List.class),
            new DynaProperty("longProperty", Long.TYPE),
            new DynaProperty("mappedProperty", Map.class),
            new DynaProperty("mappedIntProperty", Map.class),
            new DynaProperty("nullProperty", String.class),
            new DynaProperty("shortProperty", Short.TYPE),
            new DynaProperty("stringProperty", String.class),
        };
        return new BasicDynaClass("TestDynaClass", null, properties);
    }

    public void testDynaBeans() throws Exception {
        final DynaClass dynaClass = createDynaClass();
        final DynaBean dynaBean = dynaClass.newInstance();
        dynaBean.set( "stringProperty", "foo" );
        dynaBean.set( "intProperty", Integer.valueOf(24) );

        context.setVariable("dbean", dynaBean);

        assertExpression("${dbean.stringProperty}", "foo");
        assertExpression("${dbean.intProperty}", Integer.valueOf(24));
    }
}
