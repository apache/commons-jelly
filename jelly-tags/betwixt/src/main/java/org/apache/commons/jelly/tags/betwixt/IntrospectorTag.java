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

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.Converter;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a Betwixt XMLIntrospector instance that can be used by the other Betwixt tags.
 */
public class IntrospectorTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(IntrospectorTag.class);

    static {

        // register converters to standard Strategies
        ConvertUtils.register(
                new Converter() {
                    @Override
                    public Object convert(Class type, Object value) {
                        if ( value instanceof String ) {
                            return createNameMapper((String) value);
                        }
                        else if ( value == null ) {
                            return null;
                        }
                        else {
                            throw new ConversionException(
                                "Don't know how to convert: " + value
                                + " of type: " + value.getClass().getName()
                                + " into a NameMapper"
                            );
                        }
                    }
                },
                NameMapper.class
            );
    }

    /**
     * Static helper method which will convert the given string into
     * standard named strategies such as 'lowercase', 'uppercase' or 'hyphenated'
     * or use the name as a class name and create a new instance.
     */
    protected static NameMapper createNameMapper(final String name) {
        if (name.equalsIgnoreCase( "lowercase" )) {
            return new DecapitalizeNameMapper();
        }
        if (name.equalsIgnoreCase( "uppercase" )) {
            return new CapitalizeNameMapper();
        }
        if (name.equalsIgnoreCase( "hyphenated" )) {
            return new HyphenatedNameMapper();
        }
        // lets try load the class of this name
        Class theClass = null;
        try {
            theClass = Thread.currentThread().getContextClassLoader().loadClass( name );
        }
        catch (final Exception e) {
            throw new ConversionException( "Could not load class called: " + name, e );
        }
        Object object = null;
        try {
            object = theClass.getConstructor().newInstance();
        }
        catch (final Exception e) {
            throw new ConversionException( "Could not instantiate an instance of: " + name, e );
        }
        if ( object instanceof NameMapper ) {
            return (NameMapper) object;
        }
        if ( object == null ) {
            throw new ConversionException( "No NameMapper created for type: " + name );
        }
        else {
            throw new ConversionException(
                "Created object: " + object
                + " is not a NameMapper! Its type is: " + object.getClass().getName()
            );
        }
    }

    private XMLIntrospector introspector;

    private String var;

    public IntrospectorTag() {
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Factory method to create a new XMLIntrospector
     */
    protected XMLIntrospector createIntrospector() {
        return new XMLIntrospector();
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {

        if ( var == null ) {
            throw new MissingAttributeException( "var" );
        }
        invokeBody(output);

        final XMLIntrospector introspector = getIntrospector();

        context.setVariable( var, introspector );

        // now lets clear this introspector so that its recreated again next time
        this.introspector = null;
    }

    /**
     * @return the current XMLIntrospector, lazily creating one if required
     */
    public XMLIntrospector getIntrospector() {
        if ( introspector == null ) {
            introspector = createIntrospector();
        }
        return introspector;
    }

    /**
     * Sets the name mapper used for attribute names.
     * You can also use the Strings 'lowercase', 'uppercase' or 'hyphenated'
     * as aliases to the common name mapping strategies or specify a class name String.
     */
    public void setAttributeNameMapper(final NameMapper nameMapper) {
        getIntrospector().setAttributeNameMapper(nameMapper);
    }

    /**
     * Sets whether attributes or elements should be used for primitive types.
     * The default is false.
     */
    public void setAttributesForPrimitives(final boolean attributesForPrimitives) {
        getIntrospector().setAttributesForPrimitives(attributesForPrimitives);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the name mapper used for element names.
     * You can also use the Strings 'lowercase', 'uppercase' or 'hyphenated'
     * as aliases to the common name mapping strategies or specify a class name String.
     */
    public void setElementNameMapper(final NameMapper nameMapper) {
        getIntrospector().setElementNameMapper(nameMapper);
    }

    /**
     * Sets the variable name to output the new XMLIntrospector to.
     * If this attribute is not specified then this tag must be nested
     * inside an &lt;parse&gt; or &lt;output&gt; tag
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
