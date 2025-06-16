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
package org.apache.commons.jelly.tags.dynabean;

import org.apache.commons.beanutils2.DynaProperty;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * DynaProperty tag defines a property of a DynaClass
 * It can only exist inside a DynaClass parent context
 * The properties are added to the properties array
 * of the parent context, and will be used to
 * create the DynaClass object
 * @version 1.0
 */
public class PropertyTag extends TagSupport {

    private String name;
    private String type;
    private Class propertyClass;
    private DynaProperty prop;

    public PropertyTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag (final XMLOutput output) throws MissingAttributeException, JellyTagException {

        // Check that this tag is used inside the body of
        // a DynaClass tag, so that it can access the
        // context of that tag
        final DynaclassTag parentTag = (DynaclassTag) findAncestorWithClass( DynaclassTag.class );
        if ( parentTag == null ) {
            throw new JellyTagException( "This tag must be enclosed inside a <dynaclass> tag" );
        }

        // Check property name
        if (name == null) {
            throw new MissingAttributeException( "name" );
        }

        // Lookup appropriate property class
        Class propClass = propertyClass;
        if (propClass == null) {

            // Check property type
            if (type == null) {
                throw new MissingAttributeException( "type" );
            }

            if (type != null) {
                switch (type) {
                case "String":
                    propClass = String.class;
                    break;
                case "Integer":
                    propClass = Integer.TYPE;
                    break;
                case "Short":
                    propClass = Short.TYPE;
                    break;
                case "Long":
                    propClass = Long.TYPE;
                    break;
                case "Float":
                    propClass = Float.TYPE;
                    break;
                case "Double":
                    propClass = Double.TYPE;
                    break;
                default:
                    break;
                }
            }

            if (propClass == null) {
                try {
                    propClass = Class.forName(type);
                }
                catch (final Exception e) {
                    throw new JellyTagException
                            ("Class " + type +
                            " not found by Class.forName");
                }
            }
        }

        // Create dynaproperty object with given name and type
        prop = new DynaProperty (name, propClass);

        // Add new property to dynaclass context
        parentTag.addDynaProperty(prop);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the Class for this property
     */
    public Class getPropertyClass() {
        return propertyClass;
    }

    /**
     * Sets the name of this property
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the Class instance for this property
     */
    public void setPropertyClass(final Class propertyClass) {
        this.propertyClass = propertyClass;
    }

    /**
     * Sets the type name of this property
     */
    public void setType(final String type) {
        this.type = type;
    }

}
