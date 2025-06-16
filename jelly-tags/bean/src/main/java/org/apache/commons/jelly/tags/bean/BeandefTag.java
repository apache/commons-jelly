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

package org.apache.commons.jelly.tags.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties.
 */
public class BeandefTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeandefTag.class);

    /** An empty Map as I think Collections.EMPTY_MAP is only JDK 1.3 onwards */
    private static final Map EMPTY_MAP = new HashMap();

    protected static final Class[] EMPTY_ARGUMENT_TYPES = {};

    /** The name of the tag to create */
    private String name;

    /** The Java class name to use for the tag */
    private String className;

    /** The name of the invoke method */
    private String methodName;

    /** The ClassLoader used to load beans */
    private ClassLoader classLoader;

    /** The library in which to define this new bean tag */
    private final BeanTagLibrary library;

    public BeandefTag(final BeanTagLibrary library) {
        this.library = library;
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
            theClass = ClassLoaderUtils.loadClass(className, classLoader, getContext().getUseContextClassLoader(), getClass());
        } catch (final ClassNotFoundException e) {
            log.error( "Could not load class: " + className + " exception: " + e, e );
            throw new JellyTagException("Could not find class: "
                    + className
                    + " using ClassLoader: "
                    + classLoader);
        }

        final Method invokeMethod = getInvokeMethod(theClass);

        // @todo should we allow the variable name to be specified?
        library.registerBean(name, theClass, invokeMethod);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the ClassLoader to use to load classes
     *  or will use the thread context loader if none is specified.
     */
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, true, getClass());
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    protected Method getInvokeMethod(final Class theClass) {
        if (methodName != null) {
            // lets lookup the method name
            return MethodUtils.getAccessibleMethod(theClass, methodName, EMPTY_ARGUMENT_TYPES);
        }
        return null;
    }

    /**
     * @return String
     */
    public String getMethodName() {
        return methodName;
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
     * Sets the methodName.
     * @param methodName The methodName to set
     */
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    /**
     * Sets the name of the tag to create
     */
    public void setName(final String name) {
        this.name = name;
    }
}
