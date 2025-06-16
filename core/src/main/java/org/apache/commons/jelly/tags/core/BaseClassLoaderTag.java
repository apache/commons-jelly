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

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.util.ClassLoaderUtils;

/** Abstract base tag providing {@link ClassLoader} support.
  */
public abstract class BaseClassLoaderTag extends TagSupport {
    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load XMLParser itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected ClassLoader classLoader = null;

    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is {@code false}.
     */
    protected boolean useContextClassLoader = false;

    /**
     * Gets the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the XMLParser class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, useContextClassLoader, getClass());
    }

    /**
     * Gets the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /**
     * Sets the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or {@code null}
     *  to revert to the standard rules
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use JellyContext ClassLoader.
     */
    public void setUseContextClassLoader(final boolean use) {
        useContextClassLoader = use;
    }
}
