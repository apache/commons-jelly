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
package org.apache.commons.jelly.tags.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;
import org.apache.commons.beanutils2.converters.BooleanConverter;
import org.apache.commons.beanutils2.converters.ByteConverter;
import org.apache.commons.beanutils2.converters.CharacterConverter;
import org.apache.commons.beanutils2.converters.DoubleConverter;
import org.apache.commons.beanutils2.converters.FloatConverter;
import org.apache.commons.beanutils2.converters.IntegerConverter;
import org.apache.commons.beanutils2.converters.LongConverter;
import org.apache.commons.beanutils2.converters.ShortConverter;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * An argument to a {@link NewTag} or {@link InvokeTag}.
 * This tag MUST be enclosed within an {@link ArgTagParent}
 * implementation.
 */
public class ArgTag extends BaseClassLoaderTag {

    // constructors
    //-------------------------------------------------------------------------

    /** My bag of converters, by target Class */
    private static Map converterMap = new HashMap();

    // attribute setters
    //-------------------------------------------------------------------------

    // these inner classes should probably move to beanutils
    static {
        {
            final Converter c = new BooleanConverter();
            converterMap.put(Boolean.TYPE, c);
            converterMap.put(Boolean.class, c);
        }
        {
            final Converter c = new CharacterConverter();
            converterMap.put(Character.TYPE, c);
            converterMap.put(Character.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new ByteConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Byte.valueOf(((Number) value).byteValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Byte.TYPE, c);
            converterMap.put(Byte.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new ShortConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Short.valueOf(((Number) value).shortValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Short.TYPE, c);
            converterMap.put(Short.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new IntegerConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Integer.valueOf(((Number) value).intValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Integer.TYPE, c);
            converterMap.put(Integer.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new LongConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Long.valueOf(((Number) value).longValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Long.TYPE, c);
            converterMap.put(Long.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new FloatConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Float.valueOf(((Number) value).floatValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Float.TYPE, c);
            converterMap.put(Float.class, c);
        }
        {
            final Converter c = new Converter() {
                private final Converter inner = new DoubleConverter();

                @Override
                public Object convert(final Class klass, final Object value) {
                    if (value instanceof Number) {
                        return Double.valueOf(((Number) value).doubleValue());
                    }
                    return inner.convert(klass, value);
                }
            };
            converterMap.put(Double.TYPE, c);
            converterMap.put(Double.class, c);
        }
    }

    private static Object convert(final Class klass, final Object value) throws JellyTagException {
        if (null == value) {
            return null;
        } else {
            if (klass.isInstance(value)) {
                return value;
            }
            final Converter converter = (Converter) converterMap.get(klass);
            if (null == converter) {
                throw new JellyTagException("Can't convert " + value + " to " + klass);
            }
            try {
                return converter.convert(klass, value);
            } catch (final ConversionException e) {
                throw new JellyTagException("Can't convert " + value + " to " + klass + " (" + e.toString() + ")", e);
            }
        }


    }

    // tag methods
    //-------------------------------------------------------------------------

    /** The name of the parameter type, if any. */
    private String typeString;

    // private methods
    //-------------------------------------------------------------------------

    /** The value of the parameter, if any */
    private Object value;

    public ArgTag() {
    }

    // attributes
    //-------------------------------------------------------------------------

    private void assertNotNull(final Object value) throws JellyTagException {
        if (null == value) {
            throw new JellyTagException("A " + typeString + " instance cannot be null.");
        }
    }

    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);

        Class klass = null;
        if (typeString != null) {
            switch (typeString) {
            case "boolean":
                klass = Boolean.TYPE;
                assertNotNull(value);
                break;
            case "byte":
                klass = Byte.TYPE;
                assertNotNull(value);
                break;
            case "short":
                klass = Short.TYPE;
                assertNotNull(value);
                break;
            case "int":
                klass = Integer.TYPE;
                assertNotNull(value);
                break;
            case "char":
                klass = Character.TYPE;
                assertNotNull(value);
                break;
            case "float":
                klass = Float.TYPE;
                assertNotNull(value);
                break;
            case "long":
                klass = Long.TYPE;
                assertNotNull(value);
                break;
            case "double":
                klass = Double.TYPE;
                assertNotNull(value);
                break;
            default:
                if (null != typeString) {
                    try {
                      // klass = getClassLoader().loadClass(typeString);
                      // JELLY-274: rather use the three args static class-load-method
                      klass = Class.forName(typeString, false, getClassLoader());
                    } catch (final ClassNotFoundException e) {
                        throw new JellyTagException(e);
                    }
                } else if (null == value) { // and (by construction) null == typeString
                    klass = Object.class;
                } else {
                    klass = value.getClass();
                }
                break;
            }
        } else if (null != typeString) {
            try {
              // klass = getClassLoader().loadClass(typeString);
              // JELLY-274: rather use the three args static class-load-method
              klass = Class.forName(typeString, false, getClassLoader());
            } catch (final ClassNotFoundException e) {
                throw new JellyTagException(e);
            }
        } else if (null == value) { // and (by construction) null == typeString
            klass = Object.class;
        } else {
            klass = value.getClass();
        }

        if (!isInstanceOf(klass, value)) {
            if (klass.equals(Class.class)) {
                try {
                    // value = getClassLoader().loadClass((String) value);
                    // JELLY-274: rather use three-args class.forName
                    value = Class.forName((String) value, false, getClassLoader());
                } catch (final ClassNotFoundException e) {
                    throw new JellyTagException(e);
                }
            } else {
                value = convert(klass, value);
            }
        }

        final ArgTagParent parent = (ArgTagParent)findAncestorWithClass(ArgTagParent.class);
        if (null == parent) {
            throw new JellyTagException("This tag must be enclosed inside an ArgTagParent implementation (for example, <new> or <invoke>)");
        }
        parent.addArgument(klass, value);
    }

    // static stuff
    //-------------------------------------------------------------------------

    private boolean isInstanceOf(final Class klass, final Object value) {
        return null == value || klass.isInstance(value);
    }

    /**
     * The name of the argument class or type, if any.
     * This may be a fully specified class name or
     * a primitive type name
     * (<code>boolean</code>, <code>int</code>, <code>double</code>, etc.).
     */
    public void setType(final String type) {
        this.typeString = type;
    }
    /** The (possibly null) value of this argument. */
    public void setValue(final Object value) {
        this.value= value;
    }
}
