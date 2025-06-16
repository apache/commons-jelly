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
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * Performs an assertion that a given boolean expression, or XPath expression is
 * true. If the expression returns false then this test fails.
 */
public class AssertTag extends AssertTagSupport {

    /** The expression to evaluate. */
    private Expression test;

    /** The XPath expression to evaluate */
    private XPath xpath;

    public AssertTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (test == null && xpath == null) {
            throw new MissingAttributeException( "test" );
        }
        if (test != null) {
            if (! test.evaluateAsBoolean(context)) {
                fail( getBodyText(), "evaluating test: "+ test.getExpressionText() );
            }
        }
        else {
            try {
                final Object xpathContext = getXPathContext();
                if (! xpath.booleanValueOf(xpathContext)) {
                    fail( getBodyText(), "evaluating xpath: "+ xpath );
                }
            } catch (final JaxenException anException) {
                throw new JellyTagException("Error evaluating xpath", anException);
            }

        }

    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the boolean expression to evaluate. If this expression returns true
     * then the test succeeds otherwise if it returns false then the text will
     * fail with the content of the tag being the error message.
     */
    public void setTest(final Expression test) {
        this.test = test;
    }

    /**
     * Sets the boolean XPath expression to evaluate. If this expression returns true
     * then the test succeeds otherwise if it returns false then the text will
     * fail with the content of the tag being the error message.
     */
    public void setXpath(final XPath xpath) {
        this.xpath = xpath;
    }
}
