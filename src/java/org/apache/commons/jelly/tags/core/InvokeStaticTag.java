/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/InvokeStaticTag.java,v 1.1 2003/02/19 07:44:35 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2003/02/19 07:44:35 $
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
 * $Id: InvokeStaticTag.java,v 1.1 2003/02/19 07:44:35 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/** 
  * <p>
  * A Tag which can invoke a static method on a class, without an 
  * instance of the class being needed.
  * </p>
  * <p>
  * Like the {@link InvokeTag}, this tag can take a set of
  * arguments using the {@link ArgTag}.
  * </p>
  * <p>
  *  The following attributes are required:<br />
  * <ul>
  *   <li>var - The variable to assign the return of the method call to</li>
  *   <li>method - The name of the static method to invoke</li>
  *   <li>className - The name of the class containing the static method</li>
  * </ul>
  * </p>
  *
  * @author <a href="mailto:robert@bull-enterprises.com>Robert McIntosh</a>
  * @version $Revision: 1.1 $
  */
public class InvokeStaticTag extends TagSupport implements ArgTagParent {

    /** the variable exported */
    private String var;
    
    /** the method to invoke */
    private String methodName;
    
    /** the object to invoke the method on */
    private String className;
    
    private List paramTypes = new ArrayList();
    private List paramValues = new ArrayList();
    
    public InvokeStaticTag() {
    }

    /** 
     * Sets the name of the variable exported by this tag
     *
     * @param var The variable name
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Sets the name of the method to invoke
     *
     * @param method The method name
     */
    public void setMethod(String methodName) {
        this.methodName = methodName;
    }
    
    /**
     * Sets the fully qualified class name containing the static method
     *
     * @param className The name of the class
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
    /**
     * Adds an argument to supply to the method
     *
     * @param type The Class type of the argument
     * @param value The value of the argument
     */
    public void addArgument(Class type, Object value) {
        paramTypes.add(type);
        paramValues.add(value);
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws JellyTagException {
        try {        
	        if ( null == methodName) {
	            throw new MissingAttributeException( "method" );
	        }
	        invokeBody(output);
	
	        Object[] values = paramValues.toArray();
	        Class[] types = (Class[])(paramTypes.toArray(new Class[paramTypes.size()]));
	        Method method = loadClass().getMethod( methodName, types );
	        Object result = method.invoke( null, values );
	        if(null != var) {
	            context.setVariable(var, result);
	        }
	        
	        ArgTag parentArg = (ArgTag)(findAncestorWithClass(ArgTag.class));
	        if(null != parentArg) {
	            parentArg.setValue(result);
	        }
        }
        catch (ClassNotFoundException e) {
            throw createLoadClassFailedException(e);
        } 
        catch (NoSuchMethodException e) {
            throw createLoadClassFailedException(e);
        } 
        catch (IllegalAccessException e) {
            throw createLoadClassFailedException(e);
        } 
        catch (InvocationTargetException e) {
            throw createLoadClassFailedException(e);
        }
        finally {
            paramTypes.clear();
            paramValues.clear();
        }
    }
    
    // Tag interface
    //-------------------------------------------------------------------------
    
    /**
     * Loads the class using either the class loader which loaded me or the 
     * current threads context class loader
     */ 
    protected Class loadClass() throws ClassNotFoundException {
        Class theClass = getClass().getClassLoader().loadClass( className );
        if (theClass == null ) {
            theClass = Thread.currentThread().getContextClassLoader().loadClass( className );
        }
        return theClass; 
    }

	/**
	 * Factory method to create a new JellyTagException instance from a given
	 * failure exception
	 * @param e is the exception which occurred attempting to load the class
	 * @return JellyTagException
	 */
    protected JellyTagException createLoadClassFailedException(Exception e) {
        return new JellyTagException(
            "Could not load class: " + className + ". Reason: " + e, e
        );
    }
}

