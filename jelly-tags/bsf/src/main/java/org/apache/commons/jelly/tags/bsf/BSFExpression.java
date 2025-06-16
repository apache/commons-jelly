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
package org.apache.commons.jelly.tags.bsf;

import java.util.Iterator;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Represents a BSF expression
  */
public class BSFExpression extends ExpressionSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( BSFExpression.class );

    /** The expression */
    private final String text;

    /** The BSF Engine to evaluate expressions */
    private final BSFEngine engine;
    /** The BSF Manager to evaluate expressions */
    private final BSFManager manager;

    /** The adapter to BSF's ObjectRegistry that uses the JellyContext */
    private final JellyContextRegistry registry;

    public BSFExpression(final String text, final BSFEngine engine, final BSFManager manager, final JellyContextRegistry registry) {
        this.text = text;
        this.engine = engine;
        this.manager = manager;
        this.registry = registry;
    }

    @Override
    public Object evaluate(final JellyContext context) {
        // XXXX: unfortunately we must synchronize evaluations
        // so that we can swizzle in the context.
        // maybe we could create an expression from a context
        // (and so create a BSFManager for a context)
        synchronized (registry) {
            registry.setJellyContext(context);

            try {
                // XXXX: hack - there must be a better way!!!
                for ( final Iterator iter = context.getVariableNames(); iter.hasNext(); ) {
                    final String name = (String) iter.next();
                    final Object value = context.getVariable( name );
                    manager.declareBean( name, value, value.getClass() );
                }
                return engine.eval( text, -1, -1, text );
            }
            catch (final Exception e) {
                log.warn( "Caught exception evaluating: " + text + ". Reason: " + e, e );
                return null;
            }
        }
    }

    // Expression interface
    //-------------------------------------------------------------------------
    @Override
    public String getExpressionText() {
        return "${" + text + "}";
    }
}
