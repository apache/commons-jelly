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
package org.apache.commons.jelly.tags.betwixt;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * Parses some XML specified via the given URI (which can be relative or an absolute URL) and outputs the
 * parsed object.
 * <p>
 * Typically this tag is customized by setting the introspector attribute or nesting a child
 * introspector tag inside it.
 * </p>
 */
public class ParseTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTag.class);

    /** The BeanReader used to parse the XML */
    private final BeanReader reader = new BeanReader();

    private String uri;
    private String var;
    private String rootClass;
    private String path;
    private XMLIntrospector introspector;
    private boolean useContextClassLoader;
    private ClassLoader classLoader;

    public ParseTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( var == null ) {
            throw new MissingAttributeException( "var" );
        }
        if ( rootClass == null ) {
            throw new MissingAttributeException( "rootClass" );
        }

        reader.setXMLIntrospector(getIntrospector());

        Class theClass = null;
        try {
            theClass = getClassLoader().loadClass( rootClass );
        }
        catch (final Exception e) {
            throw new JellyTagException( "Could not load class called: " + rootClass, e );
        }

        if ( theClass == null ) {
            throw new JellyTagException( "Could not load class called: " + rootClass );
        }

        try {
            if ( path != null ) {
                reader.registerBeanClass( path, theClass );
            }
            else {
                reader.registerBeanClass( theClass );
            }
        }
        catch (final IntrospectionException e) {
            throw new JellyTagException(e);
        }

        Object value = null;
        if ( uri != null ) {
            invokeBody(output);

            try {
                final URL url = context.getResource( uri );
                value = reader.parse( url.toString() );
            } catch (final IOException | SAXException e) {
                throw new JellyTagException(e);
            }
        }
        else {

            // invoke the body and pass that into the reader
            final XMLOutput newOutput = new XMLOutput( reader );

            invokeBody(newOutput);

            value = reader.getRoot();
        }
        context.setVariable( var, value );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the ClassLoader to be used to load bean classes.
     */
    protected ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, useContextClassLoader, getClass());
    }

    /**
     * @return the introspector to be used, lazily creating one if required.
     */
    public XMLIntrospector getIntrospector() {
        if (introspector == null) {
            introspector = new XMLIntrospector();
        }
        return introspector;
    }

    /**
     * Sets the ClassLoader to be used to load bean classes from.
     * If this is not specified then either the ClassLoader used to load this tag library
     * is used or, if the 'useContextClassLoader' property is true, then the
     * current threads context class loader is used instead.
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Sets the Betwixt XMLIntrospector instance used to define the metadata for how a
     * bean should appear as XML.
     */
    public void setIntrospector(final XMLIntrospector introspector) {
        this.introspector = introspector;
    }

    /**
     * Sets the path that the root class should be bound to.
     * This is optional and often unnecessary though can be used to ignore some wrapping
     * elements, such as the &lt;rss&gt; element in the RSS unit test.
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Sets the name of the root class to use for parsing the XML
     */
    public void setRootClass(final String rootClass) {
        this.rootClass = rootClass;
    }

    /**
     * Sets the URI from which XML is parsed. This can be relative to this Jelly script, use
     * an absolute URI or a full URL
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * Sets whether or not the current threads's context class loader
     * should be used to load the bean classes or not.
     * This can be useful if running inside a web application or inside some
     * application server.
     */
    public void setUseContextClassLoader(final boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the variable name to output with the result of the XML parse.
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
