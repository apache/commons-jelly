/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/test/org/apache/commons/jelly/TestCoreTags.java,v 1.8 2002/05/28 07:20:06 jstrachan Exp $
 * $Revision: 1.8 $
 * $Date: 2002/05/28 07:20:06 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: TestCoreTags.java,v 1.8 2002/05/28 07:20:06 jstrachan Exp $
 */
package org.apache.commons.jelly.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Tests the use of Expression parsing
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class TestExpressions extends TestCase {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TestExpressions.class);

    protected JellyContext context = new JellyContext();
    protected ExpressionFactory factory = new JexlExpressionFactory();

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestExpressions.class);
    }

    public TestExpressions(String testName) {
        super(testName);
    }

    public void testExpresssions() throws Exception {
        context.setVariable("topping", "cheese");
        context.setVariable("type", "deepPan");
        

        assertExpression("foo", "foo");
        assertExpression("${topping}", "cheese");
        assertExpression("some${topping}", "somecheese");
        assertExpression("${topping}y", "cheesey");
        assertExpression("A ${topping} ${type} pizza", "A cheese deepPan pizza");
        assertExpression("${topping}-${type}", "cheese-deepPan");
    }
    
    protected void assertExpression(String expressionText, Object expectedValue) throws Exception {
        Expression expression = CompositeExpression.parse(expressionText, factory);
        assertTrue( "Created a valid expression for: " + expressionText, expression != null );
        Object value = expression.evaluate(context);
        //assertEquals( "Expression for: " + expressionText + " is: " + expression, expectedValue, value );
        assertEquals( "Wrong result for expression: " + expressionText, expectedValue, value );
    }
}
