/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/SetTag.java,v 1.5 2002/05/17 15:18:08 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/05/17 15:18:08 $
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
 * $Id: SetTag.java,v 1.5 2002/05/17 15:18:08 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/** A tag which sets a variable from the result of an expression 
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class SetTag extends TagSupport {

    /** The variable name to export. */
    private String var;

    /** The expression to evaluate. */
    private Expression value;

    /** The target object on which to set a property. */
    private Object target;

    /** The name of the property to set on the target object. */
    private String property;

    public SetTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        Object answer = null;
        if ( value != null ) {
            answer = value.evaluate(context);
        }
        else {
            answer = getBodyText();
        }
        
        if ( var != null ) {
            context.setVariable(var, answer);
        }
        else {
            if ( target == null ) {
                throw new JellyException( "Either a 'var' or a 'target' attribute must be defined for this tag" );
            }
            if ( property == null ) {
                throw new JellyException( "You must define a 'property' attribute if you specify a 'target'" );
            }
            setPropertyValue( target, property, value );
        }
    }

    // Properties
    //-------------------------------------------------------------------------                
    /** Sets the variable name to define for this expression
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** Sets the expression to evaluate. */
    public void setValue(Expression value) {
        this.value = value;
    }
    
    /** Sets the target object on which to set a property. */
    public void setTarget(Object target) {
        this.target = target;
    }
           
    /** Sets the name of the property to set on the target object. */
    public void setProperty(String property) {
        this.property = property;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    protected void setPropertyValue( Object target, String property, Object value ) throws Exception {
        if ( target instanceof Map ) {
            Map map = (Map) target;
            map.put( property, value );
        }
        else {
            BeanUtils.setProperty( target, property, value );       
        }
    }

}
