/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/TagLibrary.java,v 1.16 2002/10/30 19:16:26 jstrachan Exp $
 * $Revision: 1.16 $
 * $Date: 2002/10/30 19:16:26 $
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
 * $Id: TagLibrary.java,v 1.16 2002/10/30 19:16:26 jstrachan Exp $
 */

package org.apache.commons.jelly;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;

import org.xml.sax.Attributes;

/** <p><code>Taglib</code> represents the metadata for a Jelly custom tag library.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.16 $
  */

public abstract class TagLibrary {

    private Map tags = new HashMap();

    static {

        // register standard converters 
               
        ConvertUtils.register(
            new Converter() {
                public Object convert(Class type, Object value) {
                    if ( value instanceof File ) {
                        return (File) value;
                    }
                    else if ( value != null ) {
                        String text = value.toString();
                        return new File( text );
                    }
                    return null;
                }
            },
            File.class
        );
    }        

    public TagLibrary() {
    }

    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(String name, Attributes attributes)
        throws Exception {

        Object value = tags.get(name);
        if (value instanceof Class) {
            Class type = (Class) value;
            if ( type != null ) {
                return TagScript.newInstance(type);
            }
        }
        else if (value instanceof TagFactory) {
            return new TagScript( (TagFactory) value );
        }
        return null;

    }
    
    /** Creates a new Tag for the given tag name and attributes */
    public Tag createTag(String name, Attributes attributes)
        throws Exception {

        Object value = tags.get(name);
        if (value instanceof Class) {
            Class type = (Class) value;
            if ( type != null ) {
                return (Tag) type.newInstance();
            }
        }
        else if (value instanceof TagFactory) {
            TagFactory factory = (TagFactory) value;
            return factory.createTag(name, attributes);
        }
        Class type = (Class) tags.get(name);
        if ( type != null ) {
            return (Tag) type.newInstance();
        }
        return null;
    }
    
    /** Allows taglibs to use their own expression evaluation mechanism */
    public Expression createExpression(
        ExpressionFactory factory,
        String tagName,
        String attributeName,
        String attributeValue)
        throws Exception {

        ExpressionFactory myFactory = getExpressionFactory();
        if (myFactory == null) {
            myFactory = factory;
        }
        if (myFactory != null) {
            return CompositeExpression.parse(attributeValue, myFactory);
        }
        
        // will use a constant expression instead
        return new ConstantExpression(attributeValue);
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------     

    /** 
     * Registers a tag implementation Class for a given tag name 
     */
    protected void registerTag(String name, Class type) {
        tags.put(name, type);
    }

    /** 
     * Registers a tag factory for a given tag name 
     */
    protected void registerTagFactory(String name, TagFactory tagFactory) {
        tags.put(name, tagFactory);
    }

    /** Allows derived tag libraries to use their own factory */
    protected ExpressionFactory getExpressionFactory() {
        return null;
    }
    
    protected Map getTagClasses() {
        return tags;
    }

}
