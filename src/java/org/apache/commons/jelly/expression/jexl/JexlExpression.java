/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/expression/jexl/JexlExpression.java,v 1.14 2002/11/27 15:22:31 jstrachan Exp $
 * $Revision: 1.14 $
 * $Date: 2002/11/27 15:22:31 $
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
 * $Id: JexlExpression.java,v 1.14 2002/11/27 15:22:31 jstrachan Exp $
 */

package org.apache.commons.jelly.expression.jexl;

import java.util.Map;
import java.util.Set;
import java.util.Collection;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.ExpressionSupport;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.JexlContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Represents a <a href="http://jakarta.apache.org/commons/jexl.html">Jexl</a> 
 * expression which fully supports the Expression Language in JSTL and JSP 
 * along with some extra features like object method invocation.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.14 $
 */

public class JexlExpression extends ExpressionSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JexlExpression.class);

    /** The Jexl expression object */
    private Expression expression;

    public JexlExpression(Expression expression) {
        this.expression = expression;
    }

    public String toString() {
        return super.toString() + "[" + expression.getExpression() + "]";
    }
    
    // Expression interface
    //------------------------------------------------------------------------- 
    public String getExpressionText() {
        return "${" + expression.getExpression() + "}";
    }
    
    public Object evaluate(JellyContext context) {
        try {
            JexlContext jexlContext = new JellyJexlContext( context );
            if (log.isDebugEnabled()) {
                log.debug("Evaluating EL: " + expression.getExpression());
            }           
            Object value = expression.evaluate(jexlContext);
            
            if (log.isDebugEnabled()) {
                log.debug("value of expression: " + value);
            }
            
            return value;           
        }
        catch (Exception e) {
            log.warn("Caught exception evaluating: " + expression + ". Reason: " + e, e);
            return null;
        }
    }
}

class JellyJexlContext implements JexlContext {

    private Map vars;

    JellyJexlContext(JellyContext context) {
        this.vars = new JellyMap( context );
    }

    public void setVars(Map vars) {
        this.vars.clear();
        this.vars.putAll( vars );
    }

    public Map getVars() {
        return this.vars;
    }
}


class JellyMap implements Map {

    private JellyContext context;

    JellyMap(JellyContext context) {
        this.context = context;
    }

    public Object get(Object key) {
        return context.findVariable( (String) key );
    }

    public void clear() {
        // not implemented
    }

    public boolean containsKey(Object key) {
        return ( get( key ) != null );
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Set entrySet() {
        return null;
    }

    public boolean isEmpty() {
        return false;
    }

    public Set keySet() {
        return null;
    }
        
    public Object put(Object key, Object value) {
        return null;
    }

    public void putAll(Map t) {
        // not implemented
    }

    public Object remove(Object key) {
        return null;
    }

    public int size() {
        return -1;
    }

    public Collection values() {
        return null;
    }
}
