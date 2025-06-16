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
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Compares an actual object against an expected object and if they are different
 * then the test will fail.
 */
public class AssertEqualsTag extends AssertTagSupport {

    private Expression actual;
    private Expression expected;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final String message = getBodyText();

        final Object expectedValue = expected.evaluate(context);
        final Object actualValue = actual.evaluate(context);

        if (expectedValue == null && actualValue == null) {
            return;
        }
        if (actualValue != null && expectedValue.equals(actualValue)) {
            return;
        }

        final String expressions = "\nExpected expression: ("
            + expected.getExpressionText() + ")=(" + expectedValue + ")"
            + "\nActual expression: ("
            + actual.getExpressionText() + ")=(" + actualValue + ")";

        failNotEquals(message, expectedValue, actualValue, expressions);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the actual value which will be compared against the
     * expected value.
     */
    public void setActual(final Expression actual) {
        this.actual = actual;
    }

    /**
     * Sets the expected value to be tested against
     */
    public void setExpected(final Expression expected) {
        this.expected = expected;
    }
}
