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
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.xml.sax.SAXException;

/**
 * <p><code>ExpressionScript</code> outputs the value of an expression as text.</p>
 */
public class ExpressionScript implements Script {

    /** The expression evaluated as a String and output by this script */
    private Expression expression;

    public ExpressionScript() {
    }

    public ExpressionScript(final Expression expression) {
        this.expression = expression;
    }

    // Script interface
    //-------------------------------------------------------------------------
    @Override
    public Script compile() {
        return this;
    }

    /** @return the expression evaluated as a String and output by this script */
    public Expression getExpression() {
        return expression;
    }

    /** Evaluates the body of a tag */
    @Override
    public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
        final Object result = expression.evaluate(context);
        if (result != null) {

            try {
                output.objectData(result);
            } catch (final SAXException e) {
                throw new JellyTagException("Could not write to XMLOutput", e);
            }

        }
    }

    /** Sets the expression evaluated as a String and output by this script */
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return super.toString() + "[expression=" + expression + "]";
    }
}
