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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class JellyJexlContext implements JexlContext {

    private final Map vars;

    JellyJexlContext(final JellyContext context) {
        this.vars = new JellyMap( context );
    }

    @Override
    public Map getVars() {
        return this.vars;
    }

    @Override
    public void setVars(final Map vars) {
        this.vars.clear();
        this.vars.putAll( vars );
    }
}

final class JellyMap implements Map {

    private final JellyContext context;

    JellyMap(final JellyContext context) {
        this.context = context;
    }

    @Override
    public void clear() {
        // not implemented
    }

    @Override
    public boolean containsKey(final Object key) {
        return get( key ) != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        return false;
    }

    @Override
    public Set entrySet() {
        return null;
    }

    @Override
    public Object get(final Object key) {
        return context.getVariable( (String) key );
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Object put(final Object key, final Object value) {
        return null;
    }

    @Override
    public void putAll(final Map t) {
        // not implemented
    }

    @Override
    public Object remove(final Object key) {
        return null;
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public Collection values() {
        return null;
    }
}

/**
 * Represents a <a href="https://commons.apache.org/jexl/">Jexl</a>
 * expression which fully supports the Expression Language in JSTL and JSP
 * along with some extra features like object method invocation.
 */

public class JexlExpression extends ExpressionSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JexlExpression.class);

    /** The Jexl expression object */
    private final Expression expression;

    public JexlExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(final JellyContext context) {
        try {
            final JexlContext jexlContext = new JellyJexlContext( context );
            if (log.isDebugEnabled()) {
                log.debug("Evaluating EL: " + expression.getExpression());
            }
            final Object value = expression.evaluate(jexlContext);

            if (log.isDebugEnabled()) {
                log.debug("value of expression: " + value);
            }

            return value;
        }
        catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new IllegalStateException (e.getMessage(), e);
        }
    }

    // Expression interface
    //-------------------------------------------------------------------------
    @Override
    public String getExpressionText() {
        return "${" + expression.getExpression() + "}";
    }

    @Override
    public String toString() {
        return super.toString() + "[" + expression.getExpression() + "]";
    }
}
