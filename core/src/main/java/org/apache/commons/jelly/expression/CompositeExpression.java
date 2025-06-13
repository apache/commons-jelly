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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;

/**
 * <p><code>CompositeExpression</code> is a Composite expression made up of several
 * Expression objects which are concatenated into a single String.</p>
 */
public class CompositeExpression extends ExpressionSupport {

    /**
     * Parses the given String to be either a ConstantExpression, an Expression denoted as
     * "${foo}" or some String with embedded expressions such as "abc${something}def${else}xyz"
     * which results in a CompositeExpression being returned.
     *
     * @param text is the String to parse into expressions
     * @param factory is the Factory of Expression objects used to create expressions for the contents
     *  of the String "foo" inside expressions such as "${foo}"
     *
     * @return the Expression for the given String.
     * @throws JellyException if the text is invalid (such as missing '}' character).
     * @throws JellyException if there was some problem creating the underlying Expression object
     *  from the ExpressionFactory
     */
    public static Expression parse(final String text, final ExpressionFactory factory) throws JellyException {

        final int len = text.length();

        final int startIndex = text.indexOf( "${" );

        if ( startIndex < 0) {
            return new ConstantExpression(text);
        }

        final int endIndex = text.indexOf( "}", startIndex+2 );

        if ( endIndex < 0 ) {
            throw new JellyException( "Missing '}' character at the end of expression: " + text );
        }
        if ( startIndex == 0 && endIndex == len - 1 ) {
            return factory.createExpression(text.substring(2, endIndex));
        }

        final CompositeExpression answer = new CompositeExpression();

        int cur = 0;
        char c = 0;

        final StringBuilder chars = new StringBuilder();
        final StringBuilder expr  = new StringBuilder();

      MAIN:
        while ( cur < len ) {
            c = text.charAt( cur );

            switch ( c ) {
                case'$':
                    if ( cur+1<len ) {
                        if ( text.charAt( cur + 1 ) == '{' ) {
                        ++cur;
                        c = text.charAt( cur );

                                if ( chars.length() > 0 ) {
                                    answer.addTextExpression( chars.toString() );
                                    chars.delete(0, chars.length() );
                                }

                                if (cur+1<len) {
                                    ++cur;

                                    while (cur<len) {
                                        c = text.charAt(cur);
                                        switch ( c ) {
                                            case'"':
                                              expr.append( c );
                                              ++cur;

                                              DOUBLE_QUOTE:
                                                while(cur<len) {
                                                    c = text.charAt(cur);

                                                    switch ( c ) {
                                                        case'\\':
                                                            ++cur;
                                                            expr.append(c);
                                                            break;
                                                        case'"':
                                                            ++cur;
                                                            expr.append(c);
                                                            break DOUBLE_QUOTE;
                                                        default:
                                                            ++cur;
                                                            expr.append(c);
                                                    } // switch
                                                } // while
                                                break;
                                            case'\'':
                                                expr.append( c );
                                                ++cur;

                                              SINGLE_QUOTE:
                                                while(cur<len) {
                                                    c = text.charAt(cur);

                                                    switch ( c ) {
                                                        case'\\':
                                                            ++cur;
                                                            expr.append(c);
                                                            break;
                                                        case'\'':
                                                            ++cur;
                                                            expr.append(c);
                                                            break SINGLE_QUOTE;
                                                        default:
                                                            ++cur;
                                                            expr.append(c);
                                                    } // switch
                                                } // while
                                                break;
                                            case'}':
                                                answer.addExpression(factory.createExpression(expr.toString()));
                                                expr.delete(0, expr.length());
                                                ++cur;
                                                continue MAIN;
                                            default:
                                                expr.append( c );
                                                ++cur;
                                        }
                                    }
                                }
                        }
                        else if ( text.charAt( cur + 1 ) == '$' ) // $$
                        {
                            chars.append( c );
                            if ( cur + 2 < len && text.charAt( cur + 2 ) == '{' ) // $${
                            {
                                ++cur;
                            }
                        }
                        else
                        {
                            chars.append( c );
                        }
                    }
                    else
                    {
                        chars.append(c);
                    }
                    break;
                default:
                    chars.append( c );
            }
            ++cur;
        }

        if ( chars.length() > 0 )
        {
            answer.addTextExpression( chars.toString() );
        }

        return answer;
    }

    /** The expressions */
    private List expressions;

    public CompositeExpression() {
        this.expressions = new ArrayList();
    }

    public CompositeExpression(final List expressions) {
        this.expressions = expressions;
    }

    /**
     * Adds a new expression to the end of the expression list
     */
    public void addExpression(final Expression expression) {
        expressions.add(expression);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * A helper method to add a new constant text expression
     */
    public void addTextExpression(final String text) {
        addExpression(new ConstantExpression(text));
    }

    // inherit javadoc from interface
    @Override
    public Object evaluate(final JellyContext context) {
        return evaluateAsString(context);
    }

    // inherit javadoc from interface
    @Override
    public Iterator evaluateAsIterator(final JellyContext context) {
        final String value = evaluateAsString(context);
        if ( value == null ) {
            return Collections.EMPTY_LIST.iterator();
        }
        return new SingletonIterator( value );
    }

    // inherit javadoc from interface
    @Override
    public String evaluateAsString(final JellyContext context) {
        final StringBuilder buffer = new StringBuilder();
        for (final Iterator iter = expressions.iterator(); iter.hasNext(); ) {
            final Expression expression = (Expression) iter.next();
            final String value = expression.evaluateAsString(context);
            if ( value != null ) {
                buffer.append( value );
            }
        }
        return buffer.toString();

    }

    // Expression interface
    //-------------------------------------------------------------------------

    /**
     * @return the Expression objects that make up this
     * composite expression
     */
    public List getExpressions() {
        return expressions;
    }

    @Override
    public String getExpressionText() {
        final StringBuilder buffer = new StringBuilder();
        for (final Iterator iter = expressions.iterator(); iter.hasNext(); ) {
            final Expression expression = (Expression) iter.next();
            buffer.append( expression.getExpressionText() );
        }
        return buffer.toString();
    }

    /**
     * Sets the Expression objects that make up this
     * composite expression
     */
    public void setExpressions(final List expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        return super.toString() + "[expressions=" + expressions +"]";
    }
}
