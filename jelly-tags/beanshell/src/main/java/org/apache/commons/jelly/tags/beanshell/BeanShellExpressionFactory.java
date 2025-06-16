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
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;

import bsh.EvalError;

/** Represents a factory of <a href="http://www.beanshell.org">beanshell</a> expressions
  */
public class BeanShellExpressionFactory implements ExpressionFactory {

    /**
     * A helper method to return the JellyInterpreter for the given JellyContext
     */
    public static JellyInterpreter getInterpreter(final JellyContext context) throws EvalError {

        /**
         * @todo when we can unify the BeanShell and Jelly variable scopes we can share a single
         * BeanShell context for each JellyContext.
         * For now lets create a new one each time, which is slower.
         */
        final JellyInterpreter interpreter = new JellyInterpreter();
        interpreter.setJellyContext(context);
        return interpreter;
/*
        JellyInterpreter interpreter
            = (JellyInterpreter) context.getVariable( "org.apache.commons.jelly.beanshell.JellyInterpreter" );
        if ( interpreter == null ) {
            interpreter = new JellyInterpreter();
            interpreter.setJellyContext(context);
            context.setVariable( "org.apache.commons.jelly.beanshell.JellyInterpreter", interpreter );
        }
        return interpreter;
*/
    }

    // ExpressionFactory interface
    //-------------------------------------------------------------------------
    @Override
    public Expression createExpression(final String text) throws JellyException {
        return new BeanShellExpression(text);
    }
}
