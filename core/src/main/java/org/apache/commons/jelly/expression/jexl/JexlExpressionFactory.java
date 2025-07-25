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

package org.apache.commons.jelly.expression.jexl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.expression.ExpressionSupport;

//import org.apache.commons.jexl.resolver.FlatResolver;

/**
 * Represents a factory of <a href="https://commons.apache.org/jexl/">Jexl</a>
 * expression which fully supports the Expression Language in JSTL and JSP.
 * In addition this ExpressionFactory can also support Ant style variable
 * names, where '.' is used inside variable names.
 */

public class JexlExpressionFactory implements ExpressionFactory {

    private final class ExpressionSupportLocal extends ExpressionSupport {

        protected Expression jexlExpression = null;
        protected String text = null;

        public ExpressionSupportLocal(final Expression jexlExpression, final String text) {
            this.jexlExpression = jexlExpression;
            this.text = text;
        }

        @Override
        public Object evaluate(final JellyContext context) {
            Object answer = jexlExpression.evaluate(context);

            if ( answer == null ) {
                answer = context.getVariable(text);
            }

            return answer;
        }

        @Override
        public String getExpressionText() {
            return "${" + text + "}";
        }

        @Override
        public String toString() {
            return super.toString() + "[expression:" + text + "]";
        }
    }

    /** Whether we should allow Ant-style expressions, using dots as part of variable name */
    private boolean supportAntVariables = true;

    // Properties
    //-------------------------------------------------------------------------

    // ExpressionFactory interface
    //-------------------------------------------------------------------------
    @Override
    public Expression createExpression(final String text) throws JellyException {
/*

        org.apache.commons.jexl.Expression expr =
            org.apache.commons.jexl.ExpressionFactory.createExpression(text);

        if ( isSupportAntVariables() ) {
            expr.addPostResolver(new FlatResolver());
        }

        return new JexlExpression( expr );
*/

        Expression jexlExpression = null;
        try {
            // this method really does throw Exception
            jexlExpression = new JexlExpression(
            org.apache.commons.jexl.ExpressionFactory.createExpression(text)
            );
        } catch (final Exception e) {
            throw new JellyException("Unable to create expression: " + text, e);
        }

        if ( isSupportAntVariables() && isValidAntVariableName(text) ) {
            return new ExpressionSupportLocal(jexlExpression, text);
        }
        return jexlExpression;
    }

    /**
     * @return whether we should allow Ant-style expressions, using dots as
     * part of variable name
     */
    public boolean isSupportAntVariables() {
        return supportAntVariables;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * @return true if the given string is a valid Ant variable name,
     * typically thats alphanumeric text with '.' etc.
     */
    protected boolean isValidAntVariableName(final String text) {
        final char[] chars = text.toCharArray();
        for (final char ch : chars) {
            // could maybe be a bit more restrictive...
            if ( Character.isWhitespace(ch) || ch == '[' || ch == ']' || ch == '(' || ch == ')') {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets whether we should allow Ant-style expressions, using dots as
     * part of variable name
     */
    public void setSupportAntVariables(final boolean supportAntVariables) {
        this.supportAntVariables = supportAntVariables;
    }

}
