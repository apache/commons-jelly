/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/expression/Expression.java,v 1.7 2002/10/30 19:16:19 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/10/30 19:16:19 $
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
 * $Id: Expression.java,v 1.7 2002/10/30 19:16:19 jstrachan Exp $
 */
package org.apache.commons.jelly.expression;

import java.util.Iterator;

import org.apache.commons.jelly.JellyContext;

/** <p><code>Expression</code> represents an arbitrary expression using some pluggable
  * expression language.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.7 $
  */
public interface Expression {

    /** 
     * Evaluates the expression with the given context
     * and returns the result 
     */
    public Object evaluate(JellyContext context);        
    
    /**
     * Evaluates the expression with the given context
     * coercing the result to be a String.
     */
    public String evaluateAsString(JellyContext context);
    
    /**
     * Evaluates the expression with the given context
     * coercing the result to be a boolean.
     */
    public boolean evaluateAsBoolean(JellyContext context);
    
    /**
     * Evaluates the expression with the given context
     * coercing the result to be an Iterator.
     */
    public Iterator evaluateAsIterator(JellyContext context);

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
    public Object evaluateRecurse(JellyContext context);
}
