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

package org.apache.commons.jelly.tags.define;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.Attribute;
import org.apache.commons.jelly.impl.DynamicBeanTag;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties.
 */
public class BeanTag extends DefineTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTag.class);

    /** An empty Map as I think Collections.EMPTY_MAP is only JDK 1.3 onwards */
    private static final Map EMPTY_MAP = new HashMap();

    /** The name of the tag to create */
    private String name;

    /** The Java class name to use for the tag */
    private String className;

    /** The ClassLoader used to load beans */
    private ClassLoader classLoader;

    /** The name of the attribute used for the variable name */
    private String varAttribute = "var";

    /** The attribute definitions for this dynamic tag */
    private Map attributes;

    /**
     * Adds a new attribute definition to this dynamic tag
     */
    public void addAttribute(final Attribute attribute) {
        if ( attributes == null ) {
            attributes = new HashMap();
        }
        attributes.put( attribute.getName(), attribute );
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        invokeBody(output);

        if (name == null) {
            throw new MissingAttributeException("name");
        }
        if (className == null) {
            throw new MissingAttributeException("className");
        }

        Class theClass = null;
        try {
            final ClassLoader classLoader = getClassLoader();
            theClass = ClassLoaderUtils.loadClass(className, getClassLoader(), getContext().getUseContextClassLoader(), getClass());
        }
        catch (final ClassNotFoundException e) {
            log.error( "Could not load class: " + className + " exception: " + e, e );
            throw new JellyTagException(
                "Could not find class: "
                    + className
                    + " using ClassLoader: "
                    + classLoader);
        }

        final Class beanClass = theClass;
        final Method invokeMethod = getInvokeMethod( theClass );
        final Map beanAttributes = attributes != null ? attributes : EMPTY_MAP;

        final TagFactory factory = (name, attributes) -> new DynamicBeanTag(beanClass, beanAttributes, varAttribute, invokeMethod);

        getTagLibrary().registerBeanTag(name, factory);

        // now lets clear the attributes for next invocation and help the GC
        attributes = null;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the ClassLoader to use to load classes specified by this object,
     *  the thread context loader if the context flag is set, or the class used to load this class.
     */
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, getContext().getUseContextClassLoader(), getClass());
    }

    /**
     * Extracts the invoke method for the class if one is used.
     */
    protected Method getInvokeMethod( final Class theClass ) {
        return null;
    }

    /**
     * Sets the ClassLoader to use to load the class.
     * If no value is set then the current threads context class
     * loader is used.
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Sets the Java class name to use for the tag
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Sets the name of the tag to create
     */
    public void setName(final String name) {
        this.name = name;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the attribute used to define the bean variable that this dynamic
     * tag will output its results as. This defaults to 'var' though this property
     * can be used to change this if it conflicts with a bean property called 'var'.
     */
    public void setVarAttribute(final String varAttribute) {
        this.varAttribute = varAttribute;
    }
}
