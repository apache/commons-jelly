/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.betwixt;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConversionException;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.apache.commons.betwixt.strategy.NameMapper;
        

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Creates a Betwixt XMLIntrospector instance that can be used with other Betwixt tags.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class IntrospectorTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(IntrospectorTag.class);

    private XMLIntrospector introspector;
    private String var;
    
    static {

        // register converters to standard Strategies
        ConvertUtils.register( 
            new Converter() {
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


    
    public IntrospectorTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws Exception {

        invokeBody(output);        
        
        XMLIntrospector introspector = getIntrospector();
        
        if ( var == null ) {
            // the parent tag should be a <parse> or <output> tag
            IntrospectorUser tag = (IntrospectorUser) findAncestorWithClass( IntrospectorUser.class );
            if ( tag == null ) {
                throw new JellyException( 
                    "This tag must be nested inside a <parse> or <output> tag,"
                    + " or the 'var' attribute should be specified" 
                );
            }
            tag.setIntrospector( introspector );
        }
        else {
            context.setVariable( var, introspector );
        }
        
        // now lets clear this introspector so that its recreated again next time
        this.introspector = null;
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

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
     * Sets whether attributes or elements should be used for primitive types.
     * The default is false.
     */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        getIntrospector().setAttributesForPrimitives(attributesForPrimitives);
    }

    /**
     * Sets the name mapper used for element names.
     * You can also use the Strings 'lowercase', 'uppercase' or 'hyphenated' 
     * as aliases to the common name mapping strategies or specify a class name String.
     */
    public void setElementNameMapper(NameMapper nameMapper) {
        getIntrospector().setElementNameMapper(nameMapper);
    }
            
    /**
     * Sets the name mapper used for attribute names.
     * You can also use the Strings 'lowercase', 'uppercase' or 'hyphenated' 
     * as aliases to the common name mapping strategies or specify a class name String.
     */
    public void setAttributeNameMapper(NameMapper nameMapper) {
        getIntrospector().setAttributeNameMapper(nameMapper);
    }
            
    
    /**
     * Sets the variable name to output the new XMLIntrospector to.
     * If this attribute is not specified then this tag must be nested
     * inside an &lt;parse&gt; or &lt;output&gt; tag
     */
    public void setVar(String var) {
        this.var = var;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Static helper method which will convert the given string into
     * standard named strategies such as 'lowercase', 'uppercase' or 'hyphenated'
     * or use the name as a class name and create a new instance.
     */
    protected static NameMapper createNameMapper(String name) {
        if ( name.equalsIgnoreCase( "lowercase" ) ) {
            return new DecapitalizeNameMapper();
        }
        else if ( name.equalsIgnoreCase( "uppercase" ) ) {
            return new CapitalizeNameMapper();
        }
        else if ( name.equalsIgnoreCase( "hyphenated" ) ) {
            return new HyphenatedNameMapper();
        }
        else {
            // lets try load the class of this name
            Class theClass = null;
            try {
                theClass = Thread.currentThread().getContextClassLoader().loadClass( name );
            }
            catch (Exception e) {
                throw new ConversionException( "Could not load class called: " + name, e );
            }
            
            Object object = null;
            try {
                object = theClass.newInstance();
            }
            catch (Exception e) {
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
    }
    
    /**
     * Factory method to create a new XMLIntrospector
     */
    protected XMLIntrospector createIntrospector() {
        return new XMLIntrospector();
    }
}
