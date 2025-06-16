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
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Represents a <a href="http://www.beanshell.org">beanshell</a> expression
  */
public class BeanShellExpression extends ExpressionSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( BeanShellExpression.class );

    /** The expression */
    private final String text;

    public BeanShellExpression(final String text) {
        this.text = text;
    }

    @Override
    public Object evaluate(final JellyContext context) {
        try {
            final JellyInterpreter interpreter = new JellyInterpreter();
            interpreter.setJellyContext(context);

            if ( log.isDebugEnabled() ) {
                log.debug( "Evaluating beanshell: " + text );
            }

            return interpreter.eval( text );
        }
        catch (final Exception e) {
            log.warn( "Caught exception evaluating: " + text + ". Reason: " + e, e );
            return null;
        }
    }

    // Expression interface
    //-------------------------------------------------------------------------
    @Override
    public String getExpressionText() {
        return "${" + text + "}";
    }
}
