/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.impl.CollectionTag;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a bean for the given tag which is then either output as a variable
 * or can be added to a parent tag.
 * @version   $Revision$
 */
public class BeanTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTag.class);

    protected static final Object[] EMPTY_ARGUMENTS = {};

    /** The name of the property to create */
    private final String tagName;

    /** The name of the adder method */
    protected String addMethodName;

    /** If present this is used to call a doit method when the bean is constructed */
    private final Method invokeMethod;

    public BeanTag() {
        this(null, "bean", null);
    }

    public BeanTag(final Class defaultClass, final String tagName) {
        this(defaultClass, tagName, null);
    }

    public BeanTag(final Class defaultClass, final String tagName, final Method invokeMethod) {
        super(defaultClass);
        this.tagName = tagName;
        this.invokeMethod = invokeMethod;

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
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }

        // now lets try set the parent property via calling the adder or the setter method
        if (bean != null) {
            Tag parent = this;

            while (true) {
                parent = parent.getParent();
                if (parent == null) {
                    break;
                }

                if (parent instanceof BeanSource) {
                    final BeanSource source = (BeanSource) parent;
                    final Object parentObject = source.getBean();
                    if (parentObject != null) {
                        if (parentObject instanceof Collection) {
                            final Collection collection = (Collection) parentObject;
                            collection.add(bean);
                        }
                        else {
                            // lets see if there's a setter method...
                            final Method method = findAddMethod(parentObject.getClass(), bean.getClass());
                            if (method != null) {
                                final Object[] args = { bean };
                                try {
                                    method.invoke(parentObject, args);
                                }
                                catch (final Exception e) {
                                    throw new JellyTagException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                                }
                            }
                            else {
                                try {
                                  BeanUtils.setProperty(parentObject, tagName, bean);
                                } catch (final IllegalAccessException | InvocationTargetException e) {
                                    throw new JellyTagException(e);
                                }
                            }
                        }
                    }
                    else {
                        log.warn("Cannot process null bean for tag: " + parent);
                    }
                }
                else if (parent instanceof CollectionTag) {
                    final CollectionTag tag = (CollectionTag) parent;
                    tag.addItem(bean);
                }
                else {
                    continue;
                }
                break;
            }

            if (invokeMethod != null) {
                final Object[] args = { bean };
                try {
                    invokeMethod.invoke(bean, EMPTY_ARGUMENTS);
                }
                catch (final Exception e) {
                    throw new JellyTagException( "failed to invoke method: " + invokeMethod + " on bean: " + bean + " reason: " + e, e );
                }
            } else if (parent == null && var == null) {
                //warn if the bean gets lost in space
                log.warn( "Could not add bean to parent for bean: " + bean );
            }
        }
    }

    /**
     * Finds the Method to add the new bean
     */
    protected Method findAddMethod(final Class beanClass, final Class valueClass) {
        if (addMethodName == null) {
            return null;
        }
        final Class[] argTypes = { valueClass };
        return MethodUtils.getAccessibleMethod(
            beanClass, addMethodName, argTypes
        );
    }

    /**
     * @return the parent bean object
     */
    protected Object getParentObject() throws JellyTagException {
        final BeanSource tag = (BeanSource) findAncestorWithClass(BeanSource.class);
        if (tag != null) {
            return tag.getBean();
        }
        return null;
    }
}
