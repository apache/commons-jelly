/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;

import org.apache.commons.collections.BeanMap;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BeanSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag is bound onto a Java Bean class. When the tag is invoked a bean will be created
 * using the tags attributes. 
 * The bean may also have an invoke method called invoke(), run(), execute() or some such method
 * which will be invoked after the bean has been configured.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Revision: 1.7 $
 */
public class DynamicBeanTag extends DynaBeanTagSupport implements BeanSource {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DynamicBeanTag.class);

    /** Empty arguments for Method.invoke() */
    private static final Object[] emptyArgs = {};
    
    /** the bean class */
    private Class beanClass;
    
    /** the current bean instance */
    private Object bean;
    
    /** the method to invoke on the bean */
    private Method method;    

    /** 
     * the tag attribute name that is used to declare the name 
     * of the variable to export after running this tag
     */
    private String variableNameAttribute;

    /** the current variable name that the bean should be exported as */
    private String var;

    /** the set of attribute names we've already set */
    private Set setAttributesSet = new HashSet();

    /** the attribute definitions */
    private Map attributes;    
        
    public DynamicBeanTag(Class beanClass, Map attributes, String variableNameAttribute, Method method) {
        this.beanClass = beanClass;
        this.method = method;
        this.attributes = attributes;
        this.variableNameAttribute = variableNameAttribute;
    }

    public void beforeSetAttributes() throws JellyTagException {
        // create a new dynabean before the attributes are set
        try {
            bean = beanClass.newInstance();
            setDynaBean( new ConvertingWrapDynaBean( bean ) );
        } catch (InstantiationException e) {
            throw new JellyTagException("Could not instantiate dynabean",e);
        } catch (IllegalAccessException e) {
            throw new JellyTagException("Could not instantiate dynabean",e);
        }

        setAttributesSet.clear();                    
    }

    public void setAttribute(String name, Object value) throws JellyTagException {        
        boolean isVariableName = false;
        if (variableNameAttribute != null ) {
            if ( variableNameAttribute.equals( name ) ) {
                if (value == null) {
                    var = null;
                }
                else {
                    var = value.toString();
                }
                isVariableName = true;
            }
        }
        if (! isVariableName) {
            
            // #### strictly speaking we could
            // know what attributes are specified at compile time
            // so this dynamic set is unnecessary            
            setAttributesSet.add(name);
            
            // we could maybe implement attribute specific validation here
            
            super.setAttribute(name, value);
        }
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {

        // lets find any attributes that are not set and 
        for ( Iterator iter = attributes.values().iterator(); iter.hasNext(); ) {
            Attribute attribute = (Attribute) iter.next();
            String name = attribute.getName();
            if ( ! setAttributesSet.contains( name ) ) {
                if ( attribute.isRequired() ) {
                    throw new MissingAttributeException(name);
                }
                // lets get the default value
                Object value = null;
                Expression expression = attribute.getDefaultValue();
                if ( expression != null ) {
                    value = expression.evaluate(context);
                }
                
                // only set non-null values?
                if ( value != null ) {
                    super.setAttribute(name, value);
                }
            }
        }

        // If the dynamic bean is itself a tag, let it execute itself
        if (bean instanceof Tag)
        {
            Tag tag = (Tag) bean;
            tag.setBody(getBody());
            tag.setContext(getContext());
            tag.setParent(getParent());
            ((Tag) bean).doTag(output);
            
            return;
        }

        invokeBody(output);

        // export the bean if required
        if ( var != null ) {
            context.setVariable(var, bean);
        }
        
        // now, I may invoke the 'execute' method if I have one
        if ( method != null ) {
            try {
                method.invoke( bean, emptyArgs );
            }
            catch (IllegalAccessException e) {
                methodInvocationException(bean, method, e);
            }
            catch (IllegalArgumentException e) {
                methodInvocationException(bean, method, e);
            }
            catch (InvocationTargetException e) {
                // methodInvocationError(bean, method, e);

                Throwable inner = e.getTargetException();

                throw new JellyTagException(e);
                
            }
        }
    }
    
    /**
     * Report the state of the bean when method invocation fails
     * so that the user can determine any problems that might
     * be occuring while using dynamic jelly beans.
     *
     * @param bean Bean on which <code>method</code was invoked
     * @param method Method that was invoked
     * @param e Exception throw when <code>method</code> was invoked
     */
    private void methodInvocationException(Object bean, Method method, Exception e) throws JellyTagException {
        log.error("Could not invoke " + method, e);
        BeanMap beanMap = new BeanMap(bean);
        
        log.error("Bean properties:");
        for (Iterator i = beanMap.keySet().iterator(); i.hasNext();) {
            String property = (String) i.next();
            Object value = beanMap.get(property);
            log.error(property + " -> " + value);
        }
        
        log.error(beanMap);
        throw new JellyTagException(e);
    }
    
    // Properties
    //-------------------------------------------------------------------------                    
    /**
     * @return the bean that has just been created 
     */
    public Object getBean() {
        return bean;
    }
}
