/*
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
 */
package org.apache.commons.jelly.tags.core;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;

/** 
 * A tag which instantiates an instance of the given class
 * and then sets the properties on the bean.
 * The class can be specified via a {@link java.lang.Class} instance or
 * a String which will be used to load the class using either the current
 * thread's context class loader or the class loader used to load this
 * Jelly library.
 * 
 * This tag can be used it as follows, 
 * <pre>
 * &lt;j:useBean var="person" class="com.acme.Person" name="James" location="${loc}"/&gt;
 * &lt;j:useBean var="order" class="${orderClass}" amount="12" price="123.456"/&gt;
 * </pre>
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class UseBeanTag extends MapTagSupport implements BeanSource {

    /** the current bean instance */
    private Object bean;
    
    /** the default class to use if no Class is specified */
    private Class defaultClass;

    public UseBeanTag() {
    }

    public UseBeanTag(Class defaultClass) {
        this.defaultClass = defaultClass;
    }

    // BeanSource interface
    //-------------------------------------------------------------------------                    

    /**
     * @return the bean that has just been created 
     */
    public Object getBean() {
        return bean;
    }


    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        Map attributes = getAttributes();
        String var = (String) attributes.get( "var" );
        Object classObject = attributes.remove( "class" );
        
        // this method could return null in derived classes
        Class theClass = convertToClass(classObject);
        
        this.bean = newInstance(theClass, attributes, output);
        setBeanProperties(bean, attributes);
        
        // invoke body which could result in other properties being set
        invokeBody(output);
        
        processBean(var, bean);
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * Allow derived classes to programatically set the bean
     */
    protected void setBean(Object bean) {
        this.bean = bean;
    }
    
    /**
     * Attempts to convert the given object to a Class instance.
     * If the classObject is already a Class it will be returned 
     * otherwise it will be converted to a String and loaded
     * using the default class loading mechanism.
     */
    protected Class convertToClass(Object classObject) throws Exception {
        if (classObject instanceof Class) {
            return (Class) classObject;
        }
        else if ( classObject == null ) {
            Class theClass = getDefaultClass();
            if (theClass == null) {
                throw new MissingAttributeException("class");
            }
            return theClass;
        }
        else {
            String className = classObject.toString();
            return loadClass(className);
        }
    }

    /**
     * Loads the given class using the default class loading mechanism
     * which is to try use the current Thread's context class loader first
     * otherise use the class loader which loaded this class.
     */    
    protected Class loadClass(String className) throws Exception {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (Exception e) {
            return getClass().getClassLoader().loadClass(className);
        }
    }
    
    /**
     * Creates a new instance of the given class, which by default will invoke the
     * default constructor.
     * Derived tags could do something different here.
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws Exception {
        return theClass.newInstance();
    }
    
    /**
     * Sets the properties on the bean. Derived tags could implement some custom 
     * type conversion etc.
     */
    protected void setBeanProperties(Object bean, Map attributes) throws Exception {
        BeanUtils.populate(bean, attributes);
    }

    /**
     * By default this will export the bean using the given variable if it is defined.
     * This Strategy method allows derived tags to process the beans in different ways
     * such as to register this bean with its parent tag etc.
     */
    protected void processBean(String var, Object bean) throws Exception {
        if (var != null) {
            context.setVariable(var, bean);
        }
    }
    
    /**
     * Allows derived classes to provide a default bean implementation class
     */
    protected Class getDefaultClass() {
        return defaultClass;
    }
}
