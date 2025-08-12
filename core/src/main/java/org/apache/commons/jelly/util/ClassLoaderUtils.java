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

package org.apache.commons.jelly.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class to centralize the class loader management code.
 */
public class ClassLoaderUtils {

    /** Log for debug etc output */
    private static final Log log = LogFactory.getLog(ClassLoaderUtils.class);

    /**
     * Gets the loader for the given class.
     * @param clazz the class to retrieve the loader for
     * @return the class loader that loaded the provided class
     */
    public static ClassLoader getClassLoader(final Class clazz) {
        ClassLoader callersLoader = clazz.getClassLoader();
        if (callersLoader == null) {
            callersLoader = ClassLoader.getSystemClassLoader();
        }
        return callersLoader;
    }

    /**
     * Gets the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The specified class loader, if any</li>
     * <li>The thread context class loader, if it exists and {@code useContextClassLoader} is true</li>
     * <li>The class loader used to load the calling class.
     * <li>The System class loader.
     * </ul>
     */
    public static ClassLoader getClassLoader(final ClassLoader specifiedLoader, final boolean useContextClassLoader, final Class callingClass) {
        if (specifiedLoader != null) {
            return specifiedLoader;
        }
        if (useContextClassLoader) {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return classLoader;
            }
        }
        return getClassLoader(callingClass);
    }

    /**
     * Gets the class loader to be used for instantiating application objects
     * when a context class loader is not specified.  This is determined based upon the following rules:
     * <ul>
     * <li>The specified class loader, if any</li>
     * <li>The class loader used to load the calling class.
     * <li>The System class loader.
     * </ul>
     */
    public static ClassLoader getClassLoader(final ClassLoader specifiedLoader, final Class callingClass) {
        if (specifiedLoader != null) {
            return specifiedLoader;
        }
        return getClassLoader(callingClass);
    }

    /**
     * Loads the given class using the current Thread's context class loader first
     * otherwise use the class loader which loaded this class.
     */
    public static Class loadClass(final String className, final Class callingClass) throws ClassNotFoundException {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            return getClassLoader(callingClass).loadClass(className);
        }
        return loader.loadClass(className);
    }

    /**
     * Loads the given class using:
     * <ol>
     * <li>the specified classloader,</li>
     * <li>the current Thread's context class loader first, if asked</li>
     * <li>otherwise use the class loader which loaded this class.</li>
     * </ol>
     */
    public static Class loadClass(final String className, final ClassLoader specifiedLoader, final boolean useContextLoader, final Class callingClass) throws ClassNotFoundException {
        Class clazz = null;
        if (specifiedLoader != null) {
            try {
                clazz = specifiedLoader.loadClass(className);
            } catch (final ClassNotFoundException e) {
                log.debug("couldn't find class in specified loader", e);
            }
        }
        if (clazz == null && useContextLoader) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                try {
                    clazz = contextLoader.loadClass(className);
                } catch (final ClassNotFoundException e) {
                    log.debug("couldn't find class in specified loader", e);
                }
            }
        }
        if (clazz == null) {
            final ClassLoader loader = getClassLoader(callingClass);
            clazz = loader.loadClass(className);
        }
        return clazz;
    }
}
