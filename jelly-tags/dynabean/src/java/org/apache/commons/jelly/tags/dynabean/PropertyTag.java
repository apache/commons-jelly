/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/dynabean/src/java/org/apache/commons/jelly/tags/dynabean/PropertyTag.java,v 1.2 2003/01/26 00:20:39 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 00:20:39 $
 *
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
 * $Id: PropertyTag.java,v 1.2 2003/01/26 00:20:39 morgand Exp $
 */
package org.apache.commons.jelly.tags.dynabean;

import org.apache.commons.beanutils.DynaProperty;
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
 * 
 * @author Theo Niemeijer
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
    public void doTag (XMLOutput output) throws MissingAttributeException, JellyTagException {

        // Check that this tag is used inside the body of
        // a DynaClass tag, so that it can access the
        // context of that tag
        DynaclassTag parentTag = (DynaclassTag) findAncestorWithClass( DynaclassTag.class );
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
    
            if (type.equals("String")) {
                propClass = String.class;
            } 
            else if (type.equals("Integer")) {
                propClass = Integer.TYPE;
            } 
            else if (type.equals("Short")) {
                propClass = Short.TYPE;
            } 
            else if (type.equals("Long")) {
                propClass = Long.TYPE;
            } 
            else if (type.equals("Float")) {
                propClass = Float.TYPE;
            } 
            else if (type.equals("Double")) {
                propClass = Double.TYPE;
            } 
            else if (type.equals("Long")) {
                propClass = Long.TYPE;
            }
    
            if (propClass == null) {
                try {
                    propClass = Class.forName(type);
                }
                catch (Exception e) {
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
     * Sets the name of this property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type name of this property
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns the Class for this property
     */
    public Class getPropertyClass() {
        return propertyClass;
    }

    /**
     * Sets the Class instance for this property
     */
    public void setPropertyClass(Class propertyClass) {
        this.propertyClass = propertyClass;
    }

}
