/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/expression/jexl/JexlExpressionFactory.java,v 1.16 2003/10/09 21:21:28 rdonkin Exp $
 * $Revision: 1.16 $
 * $Date: 2003/10/09 21:21:28 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: JexlExpressionFactory.java,v 1.16 2003/10/09 21:21:28 rdonkin Exp $
 */

package org.apache.commons.jelly.expression.jexl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.apache.commons.jelly.expression.ExpressionFactory;

//import org.apache.commons.jexl.resolver.FlatResolver;

/** 
 * Represents a factory of <a href="http://jakarta.apache.org/commons/jexl.html">Jexl</a> 
 * expression which fully supports the Expression Language in JSTL and JSP.
 * In addition this ExpressionFactory can also support Ant style variable
 * names, where '.' is used inside variable names.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.16 $
 */

public class JexlExpressionFactory implements ExpressionFactory {

    /** whether we should allow Ant-style expresssions, using dots as part of variable name */
    private boolean supportAntVariables = true;
    
    // ExpressionFactory interface
    //------------------------------------------------------------------------- 
    public Expression createExpression(String text) throws JellyException {
/*        
        
        org.apache.commons.jexl.Expression expr = 
            org.apache.commons.jexl.ExpressionFactory.createExpression(text);
            
        if ( isSupportAntVariables() ) {
            expr.addPostResolver(new FlatResolver());
        }
        
        return new JexlExpression( expr );
*/        

        Expression jexlExpression = null;
        try {
            // this method really does throw Exception
            jexlExpression = new JexlExpression(
            org.apache.commons.jexl.ExpressionFactory.createExpression(text)
            );
        } catch (Exception e) {
            throw new JellyException("Unable to create expression: " + text, e);
        }

        if ( isSupportAntVariables() && isValidAntVariableName(text) ) {
            return new ExpressionSupportLocal(jexlExpression,text);
        }
        return jexlExpression;
    }
    
    // Properties
    //------------------------------------------------------------------------- 

    /** 
     * @return whether we should allow Ant-style expresssions, using dots as 
     * part of variable name 
     */
    public boolean isSupportAntVariables() {
        return supportAntVariables;
    }
    
    /** 
     * Sets whether we should allow Ant-style expresssions, using dots as 
     * part of variable name 
     */
    public void setSupportAntVariables(boolean supportAntVariables) {
        this.supportAntVariables = supportAntVariables;
    }

    // Implementation methods
    //------------------------------------------------------------------------- 

    /**
     * @return true if the given string is a valid Ant variable name,
     * typically thats alphanumeric text with '.' etc.
     */
    protected boolean isValidAntVariableName(String text) {
        char[] chars = text.toCharArray();            
        for (int i = 0, size = chars.length; i < size; i++ ) {
            char ch = chars[i];
            // could maybe be a bit more restrictive...
            if ( Character.isWhitespace(ch) || ch == '[' ) {
                return false;
            }
        }
        return true;
    }
    
    private class ExpressionSupportLocal extends ExpressionSupport {
        
        protected Expression jexlExpression = null;
        protected String text = null;
        
        public ExpressionSupportLocal(Expression jexlExpression, String text) {
            this.jexlExpression = jexlExpression;
            this.text = text;
        }
        
        public Object evaluate(JellyContext context) {
            Object answer = jexlExpression.evaluate(context);

            if ( answer == null ) {
                answer = context.getVariable(text);
            }

            return answer;
        }
                
        public String getExpressionText() {
            return "${" + text + "}";
        }
                
        public String toString() {
            return super.toString() + "[expression:" + text + "]";
        }
    }

}
