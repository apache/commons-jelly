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

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.lang3.StringUtils;

/** <p><code>ExpressionSupport</code>
  * an abstract base class for Expression implementations
  * which provides default implementations of some of the
  * typesafe evaluation methods.</p>
  */
public abstract class ExpressionSupport implements Expression {

    protected static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();

    // inherit javadoc from interface
    @Override
    public boolean evaluateAsBoolean(final JellyContext context) {
        final Object value = evaluateRecurse(context);
        if ( value instanceof Boolean ) {
            final Boolean b = (Boolean) value;
            return b.booleanValue();
        }
        if ( value instanceof String ) {
            // return Boolean.getBoolean( (String) value );
            final String str = (String) value;

            return str.equalsIgnoreCase( "on" )
                 ||
                 str.equalsIgnoreCase( "yes" )
                 ||
                 str.equals( "1" )
                 ||
                 str.equalsIgnoreCase( "true" );

        }
        return false;
    }

    // inherit javadoc from interface
    @Override
    public Iterator evaluateAsIterator(final JellyContext context) {
        final Object value = evaluateRecurse(context);
        if (value == null) {
            return EMPTY_ITERATOR;
        }
        if (value instanceof Iterator) {
            return (Iterator) value;
        }
        if (value instanceof List) {
            final List list = (List) value;
            return list.iterator();
        }
        if (value instanceof Map) {
            final Map map = (Map) value;
            return map.entrySet().iterator();
        }
        if (value.getClass().isArray()) {
            return new ArrayIterator( value );
        }
        if (value instanceof Enumeration) {
            return new EnumerationIterator((Enumeration ) value);
        }
        if (value instanceof Collection) {
          final Collection collection = (Collection) value;
          return collection.iterator();
        }
        if (value instanceof String) {
           String[] array = StringUtils.split((String) value, "," );
           array = StringUtils.stripAll( array );
           return new ArrayIterator( array );
        }
        // XXX: should we return single iterator?
        return new SingletonIterator( value );
    }

    // inherit javadoc from interface
    @Override
    public String evaluateAsString(final JellyContext context) {
        final Object value = evaluateRecurse(context);
        // sometimes when Jelly is used inside Maven the value
        // of an expression can actually be an expression.
        // e.g. ${foo.bar} can lookup "foo.bar" in a Maven context
        // which could actually be an expression

        if ( value != null ) {
            return value.toString();
        }
        return null;
    }

    // inherit javadoc from interface
    @Override
    public Object evaluateRecurse(final JellyContext context) {
        final Object value = evaluate(context);
        if (value instanceof Expression) {
            final Expression expression = (Expression) value;
            return expression.evaluateRecurse(context);
        }
        return value;
    }
}
