/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/taglibs/bsf/src/java/org/apache/commons/jelly/tags/bsf/BSFExpression.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/21 07:58:55 $
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
 * $Id: BSFExpression.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.bsf;

import com.ibm.bsf.BSFEngine;
import com.ibm.bsf.BSFManager;

import java.util.Iterator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.ExpressionSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** Represents a BSF expression
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class BSFExpression extends ExpressionSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( BSFExpression.class );

    /** The expression */
    private String text;
    
    /** The BSF Engine to evaluate expressions */
    private BSFEngine engine;
    /** The BSF Manager to evaluate expressions */
    private BSFManager manager;
    
    /** The adapter to BSF's ObjectRegistry that uses the JellyContext */
    private JellyContextRegistry registry;
    
    public BSFExpression(String text, BSFEngine engine, BSFManager manager, JellyContextRegistry registry) {
        this.text = text;
        this.engine = engine;
        this.manager = manager;
        this.registry = registry;
    }

    // Expression interface
    //------------------------------------------------------------------------- 
    public Object evaluate(JellyContext context) {
        // XXXX: unfortunately we must sychronize evaluations
        // so that we can swizzle in the context.
        // maybe we could create an expression from a context
        // (and so create a BSFManager for a context)
        synchronized (registry) {
            registry.setJellyContext(context);
            
            try {
                // XXXX: hack - there must be a better way!!!
                for ( Iterator iter = context.getVariableNames(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    Object value = context.getVariable( name );
                    manager.declareBean( name, value, value.getClass() );
                }
                return engine.eval( text, -1, -1, text );
            }
            catch (Exception e) {
                log.warn( "Caught exception evaluating: " + text + ". Reason: " + e, e );
                return null;
            }
        }
    }
}
