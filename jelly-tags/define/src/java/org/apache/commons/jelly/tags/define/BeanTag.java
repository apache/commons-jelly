/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/java/org/apache/commons/jelly/tags/define/BeanTag.java,v 1.1 2003/01/15 14:58:01 dion Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/15 14:58:01 $
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
 * $Id: BeanTag.java,v 1.1 2003/01/15 14:58:01 dion Exp $
 */

package org.apache.commons.jelly.tags.define;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.Attribute;
import org.apache.commons.jelly.impl.DynamicBeanTag;
import org.apache.commons.jelly.impl.TagFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;

/** 
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties..
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class BeanTag extends DefineTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTag.class);

    /** An empty Map as I think Collections.EMPTY_MAP is only JDK 1.3 onwards */
    private static final Map EMPTY_MAP = new HashMap();

    /** the name of the tag to create */
    private String name;
    
    /** the Java class name to use for the tag */
    private String className;

    /** the ClassLoader used to load beans */
    private ClassLoader classLoader;
    
    /** the name of the attribute used for the variable name */
    private String varAttribute = "var";

    /** the attribute definitions for this dynamic tag */
    private Map attributes;
    
    /**
     * Adds a new attribute definition to this dynamic tag 
     */
    public void addAttribute(Attribute attribute) {
        if ( attributes == null ) {
            attributes = new HashMap();
        }
        attributes.put( attribute.getName(), attribute );
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        invokeBody(output);
        
		if (name == null) {
			throw new MissingAttributeException("name");
		}
		if (className == null) {
			throw new MissingAttributeException("className");
		}
        
		Class theClass = null;
		try {
			ClassLoader classLoader = getClassLoader();
			theClass = classLoader.loadClass(className);
		} 
		catch (ClassNotFoundException e) {
			try {
				theClass = getClass().getClassLoader().loadClass(className);
			} 
            catch (ClassNotFoundException e2) {
				try {
					theClass = Class.forName(className);
				} 
                catch (ClassNotFoundException e3) {
                    log.error( "Could not load class: " + className + " exception: " + e, e );
					throw new JellyException(
						"Could not find class: "
							+ className
							+ " using ClassLoader: "
							+ classLoader);
				}
			}
		}
        
        final Class beanClass = theClass;
        final Method invokeMethod = getInvokeMethod( theClass );
        final Map beanAttributes = (attributes != null) ? attributes : EMPTY_MAP;
        
        TagFactory factory = new TagFactory() {
            public Tag createTag(String name, Attributes attributes) {
                return  new DynamicBeanTag(beanClass, beanAttributes, varAttribute, invokeMethod);
            }
        };
        getTagLibrary().registerBeanTag(name, factory);
        
        // now lets clear the attributes for next invocation and help the GC
        attributes = null;
	}

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** 
     * Sets the name of the tag to create
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** 
     * Sets the Java class name to use for the tag
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
    /**
     * Sets the ClassLoader to use to load the class. 
     * If no value is set then the current threads context class
     * loader is used.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * @return the ClassLoader to use to load classes
     *  or will use the thread context loader if none is specified.
     */    
    public ClassLoader getClassLoader() {
        if ( classLoader == null ) {
            ClassLoader answer = Thread.currentThread().getContextClassLoader();
            if ( answer == null ) {
                answer = getClass().getClassLoader();
            }
            return answer;
        }
        return classLoader;
    }

    /**
     * Sets the name of the attribute used to define the bean variable that this dynamic
     * tag will output its results as. This defaults to 'var' though this property
     * can be used to change this if it conflicts with a bean property called 'var'.
     */
    public void setVarAttribute(String varAttribute) {    
        this.varAttribute = varAttribute;
    }
        
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Extracts the invoke method for the class if one is used.
     */
    protected Method getInvokeMethod( Class theClass ) throws Exception {
        return null;
    }
}
