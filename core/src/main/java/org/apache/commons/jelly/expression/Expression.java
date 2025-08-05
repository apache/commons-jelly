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

import java.util.Iterator;

import org.apache.commons.jelly.JellyContext;

/** <p>{@code Expression} represents an arbitrary expression using some pluggable
  * expression language.</p>
  */
public interface Expression {

    /**
     * Evaluates the expression with the given context
     * and returns the result
     */
    Object evaluate(JellyContext context);

    /**
     * Evaluates the expression with the given context
     * coercing the result to be a boolean.
     */
    boolean evaluateAsBoolean(JellyContext context);

    /**
     * Evaluates the expression with the given context
     * coercing the result to be an Iterator.
     */
    Iterator evaluateAsIterator(JellyContext context);

    /**
     * Evaluates the expression with the given context
     * coercing the result to be a String.
     */
    String evaluateAsString(JellyContext context);

    /**
     * This method evaluates the expression until a value (a non-Expression) object
     * is returned.
     * If the expression returns another expression, then the nested expression is evaluated.
     * <p>
     * Sometimes when Jelly is used inside Maven the value
     * of an expression can actually be another expression.
     * For example if a properties file is read, the values of variables
     * can actually be expressions themselves.
     * <p>
     * e.g. ${foo.bar} can lookup "foo.bar" in a Maven context
     * which could actually be another expression.
     * <p>
     * So using this method, nested expressions can be evaluated to the
     * actual underlying value object.
     */
    Object evaluateRecurse(JellyContext context);

    /**
     * @return the textual representation of this expression
     */
    String getExpressionText();
}
