/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bean/src/java/org/apache/commons/jelly/tags/bean/BeanTag.java,v 1.3 2003/01/24 10:04:30 morgand Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/24 10:04:30 $
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
 * $Id: BeanTag.java,v 1.3 2003/01/24 10:04:30 morgand Exp $
 */

package org.apache.commons.jelly.tags.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.impl.CollectionTag;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Creates a bean for the given tag which is then either output as a variable
 * or can be added to a parent tag.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Christian Sell
 * @version   $Revision: 1.3 $
 */
public class BeanTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTag.class);


    /** the name of the property to create */
    private String tagName;

    /** the name of the adder method */
    protected String addMethodName;

    
    public BeanTag(Class defaultClass, String tagName) {
        super(defaultClass);
        this.tagName = tagName;
        
        if (tagName.length() > 0) {
            addMethodName = "add" 
                + tagName.substring(0,1).toUpperCase() 
                + tagName.substring(1);
        }
    }

    /**
     * @return the local name of the XML tag to which this tag is bound
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Output the tag as a named variable. If the parent bean has an adder or setter
     * method then invoke that to register this bean with its parent.
     */
    protected void processBean(String var, Object bean) throws JellyException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        
        // now lets try set the parent property via calling the adder or the setter method
        if (bean != null) {
            Object parentObject = getParentObject();
            if (parentObject != null) {
                if (parentObject instanceof Collection) {
                    Collection collection = (Collection) parentObject;
                    collection.add(bean);
                }
                else {
                    // lets see if there's a setter method...
                    Method method = findAddMethod(parentObject.getClass(), bean.getClass());
                    if (method != null) {
                        Object[] args = { bean };
                        try {
                            method.invoke(parentObject, args);
                        }
                        catch (Exception e) {
                            throw new JellyException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                        }
                    }
                    else {
                        try {
                          BeanUtils.setProperty(parentObject, tagName, bean);
                        } catch (IllegalAccessException e) {
                            throw new JellyException(e);
                        } catch (InvocationTargetException e) {
                            throw new JellyException(e);
                        }
                    }
                }
                
            }
            else {
                // lets try find a parent List to add this bean to
                CollectionTag tag = (CollectionTag) findAncestorWithClass(CollectionTag.class);
                if (tag != null) {
                    tag.addItem(bean);
                }
                else if(var == null) { //warn if the bean gets lost in space
                    log.warn( "Could not add bean to parent for bean: " + bean );
                }
            }
        }
            
    }

    /**
     * Finds the Method to add the new bean
     */
    protected Method findAddMethod(Class beanClass, Class valueClass) {
        if (addMethodName == null) {
            return null;
        }
        Class[] argTypes = { valueClass };
        return MethodUtils.getAccessibleMethod(
            beanClass, addMethodName, argTypes
        );
    }
        
        
    /**
     * @return the parent bean object
     */
    protected Object getParentObject() throws JellyException {
        BeanSource tag = (BeanSource) findAncestorWithClass(BeanSource.class);
        if (tag != null) {
            return tag.getBean();
        }
        return null;
    }        
}
