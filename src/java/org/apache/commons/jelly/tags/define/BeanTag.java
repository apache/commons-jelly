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
package org.apache.commons.jelly.tags.define;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;

import org.apache.commons.collections.BeanMap;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;

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
public class BeanTag extends DynaBeanTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTag.class);

    /** Empty arguments for Method.invoke() */
    private static final Object[] emptyArgs = {};
    
    /** the bean class */
    private Class beanClass;
    
    /** the current bean instance */
    private Object bean;
    
    /** the method to invoke on the bean */
    private Method method;    


    public BeanTag(Class beanClass, Method method) {
        this.beanClass = beanClass;
        this.method = method;
    }

    public void beforeSetAttributes() throws Exception {
        // create a new dynabean before the attributes are set
        bean = beanClass.newInstance();
        setDynaBean( new ConvertingWrapDynaBean( bean ) );
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        invokeBody(output);
        
        // now, I may invoke the 'execute' method if I have one
        if ( method != null ) {
            try {
                method.invoke( bean, emptyArgs );
            }
            catch (IllegalAccessException e) {
                methodInvocationError(bean, method, e);
            }
            catch (IllegalArgumentException e) {
                methodInvocationError(bean, method, e);
            }
            catch (InvocationTargetException e) {
                // methodInvocationError(bean, method, e);

                Throwable inner = e.getTargetException();

                inner.fillInStackTrace();

                if ( inner instanceof Exception )
                {
                    throw (Exception) inner;
                }
                else
                {
                    throw new JellyException( inner );
                }
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
    private void methodInvocationError(Object bean, Method method, Exception e) throws Exception {
        log.error("Could not invoke " + method, e);
        BeanMap beanMap = new BeanMap(bean);
        
        log.error("Bean properties:");
        for (Iterator i = beanMap.keySet().iterator(); i.hasNext();) {
            String property = (String) i.next();
            Object value = beanMap.get(property);
            log.error(property + " -> " + value);
        }
        
        log.error(beanMap);
        throw e;
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
