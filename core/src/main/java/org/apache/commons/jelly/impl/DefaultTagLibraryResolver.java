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
package org.apache.commons.jelly.impl;

import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>{@code DefaultTagLibraryResolver} is a default implementation
 * which attempts to interpret the URI as a String called 'jelly:className'
 * and class load the given Java class. Otherwise META-INF/services/jelly/uri
 * is searched for on the thread context's class path and, if found, that
 * class will be loaded.</p>
 */
public class DefaultTagLibraryResolver implements TagLibraryResolver {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DefaultTagLibraryResolver.class);

    private DiscoverClasses discovery;

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load this class itself, is used, based on the value of the
     * {@code useContextClassLoader} variable.
     */
    private ClassLoader classLoader;

    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is {@code false}.
     */
    private boolean useContextClassLoader = false;

    public DefaultTagLibraryResolver() {
    }

    // TagLibraryResolver interface
    //-------------------------------------------------------------------------

    /**
     * Gets the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by {@code setClassLoader()}, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     {@code useContextClassLoader} property is set to true</li>
     * <li>The class loader used to load the XMLParser class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, useContextClassLoader, getClass());
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the DiscoverClasses instance to use to locate services.
     *  This object is lazily created if it has not been configured.
     */
    public DiscoverClasses getDiscoverClasses() {
        if ( discovery == null ) {
            final ClassLoaders loaders = ClassLoaders.getAppLoaders(TagLibrary.class, getClass(), false);
            discovery = new DiscoverClasses(loaders);
        }
        return discovery;
    }

    /**
     * Gets the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /**
     * Instantiates the given class name. Otherwise an exception is logged
     * and null is returned
     */
    protected TagLibrary loadClass(final String uri, final String className) {
        try {
            final Class theClass = getClassLoader().loadClass(className);
            if ( theClass != null ) {
                return newInstance(uri, theClass);
            }
        }
        catch (final ClassNotFoundException e) {
            log.error("Could not find the class: " + className + " when trying to resolve URI: " + uri, e);
        }
        return null;
    }

    /**
     * Creates a new instance of the given TagLibrary class or
     * return null if it could not be instantiated.
     */
    protected TagLibrary newInstance(final String uri, final Class theClass) {
        try {
            final Object object = theClass.newInstance();
            if (object instanceof TagLibrary) {
                return (TagLibrary) object;
            }
            log.error(
                "The tag library object mapped to: "
                    + uri
                    + " is not a TagLibrary. Object = "
                    + object);
        }
        catch (final Exception e) {
            log.error(
                "Could not instantiate instance of class: " + theClass.getName() + ". Reason: " + e,
                e);
        }
        return null;
    }

    /**
     * Attempts to resolve the given URI to be associated with a TagLibrary
     * otherwise null is returned to indicate no tag library could be found
     * so that the namespace URI should be treated as just vanilla XML.
     */
    @Override
    public TagLibrary resolveTagLibrary(final String uri) {
        final DiscoverClasses discovery = getDiscoverClasses();
        String name = uri;
        if ( uri.startsWith( "jelly:" ) ) {
            name = "jelly." + uri.substring(6);
        }

        log.info( "Looking up service name: " + name );

/*
        ClassLoaders loaders = ClassLoaders.getAppLoaders(TagLibrary.class, getClass(), false);

        DiscoverClass discover = new DiscoverClass(loaders);
        Class implClass = discover.find(TestInterface2.class);

        TagLibrary answer = null;
        try {
            answer = (TagLibrary) DiscoverSingleton.find(TagLibrary.class, name);
        }
        catch (Exception e) {
            log.error( "Could not load service: " + name );
        }
        return answer;
*/
        final ResourceClassIterator iter = discovery.findResourceClasses(name);
        while (iter.hasNext()) {
            final ResourceClass resource = iter.nextResourceClass();
            try {
                final Class typeClass = resource.loadClass();
                if ( typeClass != null ) {
                    return newInstance(uri, typeClass);
                }
            }
            catch (final Exception e) {
                log.error( "Could not load service: " + resource );
            }
        }
        log.info( "Could not find any services for name: " + name );
        return null;
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

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the fully configured DiscoverClasses instance to be used to
     * lookup services
     */
    public void setDiscoverClasses(final DiscoverClasses discovery) {
        this.discovery = discovery;
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling {@code Thread.currentThread().getContextClassLoader()})
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
