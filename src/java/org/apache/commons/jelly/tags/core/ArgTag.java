/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ArgTag.java,v 1.5 2003/09/12 01:16:32 dion Exp $
 * $Revision: 1.5 $
 * $Date: 2003/09/12 01:16:32 $
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
 * $Id: ArgTag.java,v 1.5 2003/09/12 01:16:32 dion Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/** 
 * An argument to a {@link NewTag} or {@link InvokeTag}.
 * This tag MUST be enclosed within an {@link ArgTagParent} 
 * implementation.
 *
 * @author Rodney Waldhoff
 * @version $Revision: 1.5 $
 */
public class ArgTag extends BaseClassLoaderTag {

    // constructors
    //-------------------------------------------------------------------------
    
    public ArgTag() {
    }

    // attribute setters
    //-------------------------------------------------------------------------

    /** 
     * The name of the argument class or type, if any. 
     * This may be a fully specified class name or 
     * a primitive type name
     * (<code>boolean<code>, <code>int</code>, <code>double</code>, etc.).
     */
    public void setType(String type) {
        this.typeString = type;
    }
    
    /** The (possibly null) value of this argument. */
    public void setValue(Object value) {
        this.value= value;
    }

    // tag methods
    //-------------------------------------------------------------------------

    public void doTag(XMLOutput output) throws JellyTagException {
        invokeBody(output);

        Class klass = null;
        if("boolean".equals(typeString)) {                
            klass = Boolean.TYPE;
            assertNotNull(value);            
        } else if("byte".equals(typeString)) {
            klass = Byte.TYPE;
            assertNotNull(value);
        } else if("short".equals(typeString)) {
            klass = Short.TYPE;
            assertNotNull(value);
        } else if("int".equals(typeString)) {
            klass = Integer.TYPE;
            assertNotNull(value);
        } else if("char".equals(typeString)) {
            klass = Character.TYPE;
            assertNotNull(value);
        } else if("float".equals(typeString)) {
            klass = Float.TYPE;
            assertNotNull(value);
        } else if("long".equals(typeString)) {
            klass = Long.TYPE;
            assertNotNull(value);
        } else if("double".equals(typeString)) {
            klass = Double.TYPE;
            assertNotNull(value);
        } else if(null != typeString) {
            try {
              klass = getClassLoader().loadClass(typeString);
            } catch (ClassNotFoundException e) {
                throw new JellyTagException(e);
            }
        } else if(null == value) { // and (by construction) null == typeString
            klass = Object.class;
        } else {
            klass = value.getClass();
        }

        if(!isInstanceOf(klass,value)) {
            if (klass.equals(Class.class))
            {
                try {
                    value = getClassLoader().loadClass((String) value);
                } catch (ClassNotFoundException e) {
                    throw new JellyTagException(e);
                }
            }
            else
            {
                value = convert(klass,value);
            }
        }
        
        ArgTagParent parent = (ArgTagParent)findAncestorWithClass(ArgTagParent.class);
        if(null == parent) {
            throw new JellyTagException("This tag must be enclosed inside an ArgTagParent implementation (for example, <new> or <invoke>)" );        
        } else {
            parent.addArgument(klass,value);
        }
    }

    // private methods
    //-------------------------------------------------------------------------

    private void assertNotNull(Object value) throws JellyTagException {
        if(null == value) {
            throw new JellyTagException("A " + typeString + " instance cannot be null.");
        }
    }

    private boolean isInstanceOf(Class klass, Object value) {
        return (null == value || (klass.isInstance(value)));
    }

    // attibutes
    //-------------------------------------------------------------------------

    /** The name of the parameter type, if any. */
    private String typeString;
    
    /** The value of the parameter, if any */
    private Object value;

    // static stuff
    //-------------------------------------------------------------------------

    private static Object convert(Class klass, Object value) throws JellyTagException {
        if(null == value) {
            return null;
        } else if(!klass.isInstance(value)) {
            Converter converter = (Converter)(converterMap.get(klass));
            if(null == converter) { 
                throw new JellyTagException("Can't convert " + value + " to " + klass);
            } else {
                try {
                    return converter.convert(klass,value);
                } catch(ConversionException e) {
                    throw new JellyTagException("Can't convert " + value + " to " + klass + " (" + e.toString() + ")",e);
                }
            }
        } else {
            return value;
        }
        
    }
        
    /** My bag of converters, by target Class */
    private static Map converterMap = new HashMap();
    // these inner classes should probably move to beanutils
    static {
        {
            Converter c = new BooleanConverter();
            converterMap.put(Boolean.TYPE,c);
            converterMap.put(Boolean.class,c);
        }
        {
            Converter c = new CharacterConverter();
            converterMap.put(Character.TYPE,c);
            converterMap.put(Character.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Byte(((Number)value).byteValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new ByteConverter();     
            };
            converterMap.put(Byte.TYPE,c);
            converterMap.put(Byte.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Short(((Number)value).shortValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new ShortConverter();     
            };
            converterMap.put(Short.TYPE,c);
            converterMap.put(Short.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Integer(((Number)value).intValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new IntegerConverter();     
            };
            converterMap.put(Integer.TYPE,c);
            converterMap.put(Integer.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Long(((Number)value).longValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new LongConverter();     
            };
            converterMap.put(Long.TYPE,c);
            converterMap.put(Long.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Float(((Number)value).floatValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new FloatConverter();     
            };
            converterMap.put(Float.TYPE,c);
            converterMap.put(Float.class,c);
        }
        {
            Converter c = new Converter() {
                public Object convert(Class klass, Object value) {
                    if(value instanceof Number) {
                        return new Double(((Number)value).doubleValue());
                    } else {
                        return inner.convert(klass,value);
                    }                        
                }           
                private Converter inner = new DoubleConverter();     
            };
            converterMap.put(Double.TYPE,c);
            converterMap.put(Double.class,c);
        }       
    }
}
