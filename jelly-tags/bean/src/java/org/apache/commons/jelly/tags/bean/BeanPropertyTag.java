/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bean/src/java/org/apache/commons/jelly/tags/bean/BeanPropertyTag.java,v 1.6 2003/02/25 22:54:22 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2003/02/25 22:54:22 $
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
 * $Id: BeanPropertyTag.java,v 1.6 2003/02/25 22:54:22 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Creates a nested property via calling a beans createFoo() method then
 * either calling the setFoo(value) or addFoo(value) methods in a similar way
 * to how Ant tags construct themselves.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Christian Sell
 * @version $Revision: 1.6 $
 */
public class BeanPropertyTag extends BeanTag {

    /** empty arguments constant */
    private static final Object[] EMPTY_ARGS = {};
    
    /** empty argument types constant */
    private static final Class[] EMPTY_ARG_TYPES = {};

    /** the name of the create method */
    private String createMethodName;

    
    public BeanPropertyTag(String tagName) {
        super(Object.class, tagName);

        if (tagName.length() > 0) {
            createMethodName = "create" 
                + tagName.substring(0,1).toUpperCase() 
                + tagName.substring(1);
        }
    }
    
    /**
     * Creates a new instance by calling a create method on the parent bean
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws JellyTagException {
        Object parentObject = getParentObject();
        if (parentObject != null) {
            // now lets try call the create method...
            Class parentClass = parentObject.getClass();
            Method method = findCreateMethod(parentClass);
            if (method != null) {
                try {
                    return method.invoke(parentObject, EMPTY_ARGS);
                }
                catch (Exception e) {
                    throw new JellyTagException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                }
            }
            else {
                Class tagClass = theClass;
                if(tagClass == Object.class)
                    tagClass = findAddMethodClass(parentClass);
                if(tagClass == null)
                    throw new JellyTagException("unable to infer element class for tag "+getTagName());

                return super.newInstance(tagClass, attributes, output) ;
            }
        }
        throw new JellyTagException("The " + getTagName() + " tag must be nested within a tag which maps to a BeanSource implementor");
    }

    /**
     * finds the parameter type of the first public method in the parent class whose name
     * matches the add{tag name} pattern, whose return type is void and which takes
     * one argument only.
     * @param parentClass
     * @return the class of the first and only parameter
     */
    protected Class findAddMethodClass(Class parentClass) {
        Method[] methods = parentClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if(Modifier.isPublic(method.getModifiers())) {
                Class[] args = method.getParameterTypes();
                if (method.getName().equals(addMethodName)
                      && java.lang.Void.TYPE.equals(method.getReturnType())
                      && args.length == 1
                      && !java.lang.String.class.equals(args[0])
                      && !args[0].isArray()
                      && !args[0].isPrimitive())
                    return args[0];
            }
        }
        return null;
    }

    /**
     * Finds the Method to create a new property object
     */
    protected Method findCreateMethod(Class theClass) {
        if (createMethodName == null) {
            return null;
        }
        return MethodUtils.getAccessibleMethod(
            theClass, createMethodName, EMPTY_ARG_TYPES
        );
    }    
}
