/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bean/src/java/org/apache/commons/jelly/tags/bean/BeandefTag.java,v 1.5 2003/10/09 21:21:15 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 21:21:15 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: BeandefTag.java,v 1.5 2003/10/09 21:21:15 rdonkin Exp $
 */

package org.apache.commons.jelly.tags.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties..
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.5 $
 */
public class BeandefTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeandefTag.class);

    /** An empty Map as I think Collections.EMPTY_MAP is only JDK 1.3 onwards */
    private static final Map EMPTY_MAP = new HashMap();
    
    protected static final Class[] EMPTY_ARGUMENT_TYPES = {};

    /** the name of the tag to create */
    private String name;
    
    /** the Java class name to use for the tag */
    private String className;

	/** the name of the invoke method */
	private String methodName;
	 
    /** the ClassLoader used to load beans */
    private ClassLoader classLoader;
    
    /** the library in which to define this new bean tag */
    private BeanTagLibrary library;
    
    public BeandefTag(BeanTagLibrary library) {
        this.library = library;
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
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
					throw new JellyTagException(
						"Could not find class: "
							+ className
							+ " using ClassLoader: "
							+ classLoader);
				}
			}
		}
		
		Method invokeMethod = getInvokeMethod(theClass);
        
        // @todo should we allow the variable name to be specified?
        library.registerBean(name, theClass, invokeMethod);
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
     * @return String
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the methodName.
     * @param methodName The methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected Method getInvokeMethod(Class theClass) {
        if (methodName != null) {
            // lets lookup the method name
            return MethodUtils.getAccessibleMethod(theClass, methodName, EMPTY_ARGUMENT_TYPES);
        }
        return null;
    }
}
