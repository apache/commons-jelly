/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/expression/ConstantExpression.java,v 1.4 2002/05/17 15:18:15 jstrachan Exp $
 * $Revision: 1.4 $
 * $Date: 2002/05/17 15:18:15 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: ConstantExpression.java,v 1.4 2002/05/17 15:18:15 jstrachan Exp $
 */
package org.apache.commons.jelly.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.SingletonIterator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;

/** 
 * <p><code>CompositeExpression</code> is a Composite expression made up of several
 * Expression objects which are concatenated into a single String.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.4 $
 */
public class CompositeExpression extends ExpressionSupport {

    /** The expressions */
    private List expressions;
    
    public CompositeExpression() {
        this.expressions = new ArrayList();
    }
    
    public CompositeExpression(List expressions) {
        this.expressions = expressions;
    }
    
    public String toString() {
        return super.toString() + "[expressions=" + expressions +"]";
    }
    
    /**
     * Parses the given String to be either a ConstantExpresssion, an Expression denoted as
     * "${foo}" or some String with embedded expresssions such as "abc${something}def${else}xyz"
     * which results in a CompositeExpression being returned.
     * 
     * @param text is the String to parse into expressions
     * @param factory is the Factory of Expression objects used to create expresssions for the contents
     *  of the String "foo" inside expressions such as "${foo}"
     * 
     * @return the Expresssion for the given String.
     * @throws JellyException if the text is invalid (such as missing '}' character).
     * @throws Exception if there was some problem creating the underlying Expression object 
     *  from the ExpressionFactory
     */
    public static Expression parse(String text, ExpressionFactory factory) throws JellyException {

        int len = text.length();

        int startIndex = text.indexOf( "${" );

        if ( startIndex < 0) {
            return new ConstantExpression(text);
        }

        int endIndex = text.indexOf( "}", startIndex+2 );

        if ( endIndex < 0 ) {
            throw new JellyException( "Missing '}' character at the end of expression: " + text );
        }
        if ( startIndex == 0 && endIndex == len - 1 ) {
            return factory.createExpression(text.substring(2, endIndex));
        }

        CompositeExpression answer = new CompositeExpression();

        int cur = 0;
        char c = 0;

        StringBuffer chars = new StringBuffer();
        StringBuffer expr  = new StringBuffer();

      MAIN:
        while ( cur < len )
        {
            c = text.charAt( cur );

            switch ( c )
            {
                case('$'):
                {
                    if ( cur+1<len )
                    {
                        ++cur;
                        c = text.charAt( cur );

                        switch ( c )
                        {
                            case('$'):
                            {
                                chars.append( c );
                                break;
                            }
                            case('{'):
                            {
                                if ( chars.length() > 0 )
                                {
                                    answer.addTextExpression( chars.toString() );
                                    chars.delete(0, chars.length() );
                                }

                                if (cur+1<len)
                                {
                                    ++cur;

                                    while (cur<len)
                                    {
                                        c = text.charAt(cur);
                                        {
                                            switch ( c )
                                            {
                                                case('"'):
                                                {
                                                    expr.append( c );
                                                    ++cur;

                                                  DOUBLE_QUOTE:
                                                    while(cur<len)
                                                    {
                                                        c = text.charAt(cur);

                                                        boolean escape = false;

                                                        switch ( c )
                                                        {
                                                            case('\\'):
                                                            {
                                                                escape = true;
                                                                ++cur;
                                                                expr.append(c);
                                                                break;
                                                            }
                                                            case('"'):
                                                            {
                                                                ++cur;
                                                                expr.append(c);
                                                                break DOUBLE_QUOTE;
                                                            }
                                                            default:
                                                            {
                                                                escape=false;
                                                                ++cur;
                                                                expr.append(c);
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                                case('\''):
                                                {
                                                    expr.append( c );
                                                    ++cur;

                                                  SINGLE_QUOTE:
                                                    while(cur<len)
                                                    {
                                                        c = text.charAt(cur);

                                                        boolean escape = false;

                                                        switch ( c )
                                                        {
                                                            case('\\'):
                                                            {
                                                                escape = true;
                                                                ++cur;
                                                                expr.append(c);
                                                                break;
                                                            }
                                                            case('\''):
                                                            {
                                                                ++cur;
                                                                expr.append(c);
                                                                break SINGLE_QUOTE;
                                                            }
                                                            default:
                                                            {
                                                                escape=false;
                                                                ++cur;
                                                                expr.append(c);
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                                case('}'):
                                                {
                                                    answer.addExpression(factory.createExpression(expr.toString()));
                                                    expr.delete(0, expr.length());
                                                    ++cur;
                                                    continue MAIN;
                                                }
                                                default:
                                                {
                                                    expr.append( c );
                                                    ++cur;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            default:
                            {
                                chars.append(c);
                            }
                        }
                    }
                    else
                    {
                        chars.append(c);
                    }
                    break;
                }
                default:
                {
                    chars.append( c );
                }
            }
            ++cur;
        }

        if ( chars.length() > 0 )
        {
            answer.addTextExpression( chars.toString() );
        }

        return answer;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the Expression objects that make up this 
     * composite expression
     */
    public List getExpressions() {
        return expressions;
    }

    /**
     * Sets the Expression objects that make up this 
     * composite expression
     */
    public void setExpressions(List expressions) {
        this.expressions = expressions;
    }
        
    /** 
     * Adds a new expression to the end of the expression list
     */
    public void addExpression(Expression expression) {
        expressions.add(expression);
    }
    
    /**
     * A helper method to add a new constant text expression 
     */
    public void addTextExpression(String text) {
        addExpression(new ConstantExpression(text));
    }
    
    // Expression interface
    //-------------------------------------------------------------------------
    
    public String getExpressionText() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = expressions.iterator(); iter.hasNext(); ) {
            Expression expression = (Expression) iter.next();
            buffer.append( expression.getExpressionText() );
        }
        return buffer.toString();
    }
    
        
    // inherit javadoc from interface
    public Object evaluate(JellyContext context) {
        return evaluateAsString(context);
    }
    
    // inherit javadoc from interface
    public String evaluateAsString(JellyContext context) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = expressions.iterator(); iter.hasNext(); ) {
            Expression expression = (Expression) iter.next();
            String value = expression.evaluateAsString(context);
            if ( value != null ) {
                buffer.append( value );
            }
        }
        return buffer.toString();
        
    }
    
    // inherit javadoc from interface
    public Iterator evaluateAsIterator(JellyContext context) {
        String value = evaluateAsString(context);
        if ( value == null ) {
            return Collections.EMPTY_LIST.iterator();
        }
        else {
            return new SingletonIterator( value );
        }
    }
}
