/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ArgTag.java,v 1.1 2002/11/28 00:22:23 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/11/28 00:22:23 $
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
 * $Id: ArgTag.java,v 1.1 2002/11/28 00:22:23 rwaldhoff Exp $
 */
package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/** An argument to a {@link NewTag} or {@link InvokeTag}.
  * This tag MUST be enclosed within an {@link ArgTagParent} 
  * implementation.
  *
  * @author Rodney Waldhoff
  * @version $Revision: 1.1 $
  */
public class ArgTag extends BaseClassLoaderTag {
    
    /** The name of the parameter type, if any. */
    private String typeString;
    
    /** An {@link Expression} describing the parameter value. */
    private Expression valueExpression;
    
    /** The parameter value as {@link #setValueObject set} by some child tag (if any). */
    private Object valueObject;
        
    public ArgTag() {
    }

    /** The name of the parameter type, if any. */
    public void setType(String type) {
        this.typeString = type;
    }
    
    /** The parameter value. */
    public void setValue(Expression value) {
        this.valueExpression= value;
    }
    
    /** (used by child tags) */
    public void setValueObject(Object object) {
        this.valueObject = object;
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        invokeBody(output);
        if(null != valueObject && null != valueExpression) {
            throw new JellyException("Either the value parameter or a value-setting child element can be provided, but not both.");
        }
        Class klass = null;
        Object value = valueObject;
        if(null == value && null != valueExpression) {
            value = valueExpression.evaluate(context);
        }
        if("boolean".equals(typeString)) {                
            klass = Boolean.TYPE;
            assertNotNull(value);
            if(!(value instanceof Boolean) && null != valueExpression) {
                value = new Boolean(valueExpression.evaluateAsBoolean(context));                
            }
            assertInstanceOf(Boolean.class,value);
        } else if("byte".equals(typeString)) {
            klass = Byte.TYPE;
            assertNotNull(value);
            if(!(value instanceof Byte) && null != valueExpression) {
                value = new Byte(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Byte.class,value);
        } else if("short".equals(typeString)) {
            klass = Short.TYPE;
            assertNotNull(value);
            if(!(value instanceof Short) && null != valueExpression) {
                value = new Short(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Short.class,value);
        } else if("int".equals(typeString)) {
            klass = Integer.TYPE;
            assertNotNull(value);
            if(!(value instanceof Integer) && null != valueExpression) {
                value = new Integer(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Integer.class,value);
        } else if("char".equals(typeString)) {
            klass = Character.TYPE;
            assertNotNull(value);
            if(!(value instanceof Character) && null != valueExpression) {
                value = new Character(valueExpression.evaluateAsString(context).charAt(0));
            }            
            assertInstanceOf(Character.class,value);
        } else if("float".equals(typeString)) {
            klass = Float.TYPE;
            assertNotNull(value);
            if(!(value instanceof Float) && null != valueExpression) {
                value = new Float(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Float.class,value);
        } else if("long".equals(typeString)) {
            klass = Long.TYPE;
            assertNotNull(value);
            if(!(value instanceof Long) && null != valueExpression) {
                value = new Long(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Long.class,value);
        } else if("double".equals(typeString)) {
            klass = Double.TYPE;
            assertNotNull(value);
            if(!(value instanceof Double) && null != valueExpression) {
                value = new Double(valueExpression.evaluateAsString(context));
            }            
            assertInstanceOf(Double.class,value);
        } else if(null != typeString) {
            klass = getClassLoader().loadClass(typeString);
            assertInstanceOf(klass,value);
        } else if(null == value) {
            klass = Object.class;
        } else {
            klass = value.getClass();
        }
        
        ArgTagParent parent = (ArgTagParent)findAncestorWithClass(ArgTagParent.class);
        if(null == parent) {
            throw new JellyException("This tag must be enclosed inside an ArgTagParent implementation (for example, <new> or <invoke>)" );        
        } else {
            parent.addArgument(klass,value);
        }
    }

    private void assertNotNull(Object value) throws JellyException {
        if(null == value) {
            throw new JellyException("A " + typeString + " instance cannot be null.");
        }
    }

    private void assertInstanceOf(Class klass, Object value) throws JellyException {
        if(null != klass && null != value && (!klass.isInstance(value))) {
            if(null != valueExpression) {
                throw new JellyException("Can't create a " + typeString + " instance from the expression " + valueExpression);
            } else {
                throw new JellyException("Can't create a " + typeString + " instance from the object " + valueObject);
            }
        }
    }
}
