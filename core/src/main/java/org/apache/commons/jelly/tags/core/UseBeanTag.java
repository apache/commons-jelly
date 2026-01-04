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

package org.apache.commons.jelly.tags.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.util.ClassLoaderUtils;

/**
 * A tag which instantiates an instance of the given class and then sets the properties on the bean. The class can be specified via a {@link Class} instance or
 * a String which will be used to load the class using either the current thread's context class loader or the class loader used to load this Jelly library.
 *
 * This tag can be used it as follows,
 *
 * <pre>
 * &lt;j:useBean var="person" class="com.acme.Person" name="James" location="${loc}"/&gt;
 * &lt;j:useBean var="order" class="${orderClass}" amount="12" price="123.456"/&gt;
 * </pre>
 */
public class UseBeanTag extends MapTagSupport implements BeanSource {

    /** The current bean instance */
    private Object bean;

    /** The default class to use if no Class is specified */
    private Class defaultClass;

    /**
     * a Set of Strings of property names to ignore (remove from the Map of attributes before passing to ConvertUtils)
     */
    private Set ignoreProperties;

    /**
     * If this tag finds an attribute in the XML that's not ignored by {@link #ignoreProperties} and isn't a bean property, should it throw an exception?
     *
     * @see #setIgnoreUnknownProperties(boolean)
     */
    private boolean ignoreUnknownProperties = false;

    public UseBeanTag() {
    }

    public UseBeanTag(final Class defaultClass) {
        this.defaultClass = defaultClass;
    }
    // BeanSource interface
    // -------------------------------------------------------------------------

    /**
     * Adds a name to the Set of property names that will be skipped when setting bean properties. In other words, names added here won't be set into the bean
     * if they're present in the attribute Map.
     *
     * @param name
     */
    protected void addIgnoreProperty(final String name) {
        getIgnorePropertySet().add(name);
    }

    /**
     * Attempts to convert the given object to a Class instance. If the classObject is already a Class it will be returned otherwise it will be converted to a
     * String and loaded using the default class loading mechanism.
     */
    protected Class convertToClass(final Object classObject) throws MissingAttributeException, ClassNotFoundException {
        if (classObject instanceof Class) {
            return (Class) classObject;
        }
        if (classObject != null) {
            final String className = classObject.toString();
            return loadClass(className);
        }
        final Class theClass = getDefaultClass();
        if (theClass == null) {
            throw new MissingAttributeException("class");
        }
        return theClass;
    }

    // Tag interface
    // -------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final Map attributes = getAttributes();
        final String var = (String) attributes.get("var");
        final Object classObject = attributes.get("class");
        addIgnoreProperty("class");
        addIgnoreProperty("var");
        try {
            // this method could return null in derived classes
            final Class theClass = convertToClass(classObject);
            bean = newInstance(theClass, attributes, output);
            setBeanProperties(bean, attributes);
            // invoke body which could result in other properties being set
            invokeBody(output);
            processBean(var, bean);
        } catch (final ClassNotFoundException e) {
            throw new JellyTagException(e);
        }
    }
    // Implementation methods
    // -------------------------------------------------------------------------

    /**
     * @return the bean that has just been created
     */
    @Override
    public Object getBean() {
        return bean;
    }

    /**
     * Allows derived classes to provide a default bean implementation class
     */
    protected Class getDefaultClass() {
        return defaultClass;
    }

    /**
     * @return the Set of property names that should be ignored when setting the properties of the bean.
     */
    protected Set getIgnorePropertySet() {
        if (ignoreProperties == null) {
            ignoreProperties = new HashSet();
        }
        return ignoreProperties;
    }

    /**
     * Tests If this tag finds an attribute in the XML that's not ignored by {@link #ignoreProperties} and isn't a bean property, should it throw an exception?
     *
     * @see #setIgnoreUnknownProperties(boolean)
     * @see #setIgnoreUnknownProperties(boolean)
     * @return whether to ignore unknown properties.
     */
    public boolean isIgnoreUnknownProperties() {
        return ignoreUnknownProperties;
    }

    /**
     * Loads the given class using the default class loading mechanism which is to try use the current Thread's context class loader first otherwise use the
     * class loader which loaded this class.
     */
    protected Class loadClass(final String className) throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(className, getClass());
    }

    /**
     * Creates a new instance of the given class, which by default will invoke the default constructor. Derived tags could do something different here.
     */
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output) throws JellyTagException {
        try {
            return theClass.getConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new JellyTagException(e.toString());
        }
    }

    /**
     * By default this will export the bean using the given variable if it is defined. This Strategy method allows derived tags to process the beans in
     * different ways such as to register this bean with its parent tag etc.
     */
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        } else {
            final ArgTag parentArg = (ArgTag) findAncestorWithClass(ArgTag.class);
            if (null != parentArg) {
                parentArg.setValue(bean);
            }
        }
    }

    /**
     * Allow derived classes to programmatically set the bean
     */
    protected void setBean(final Object bean) {
        this.bean = bean;
    }

    /**
     * Sets the properties on the bean. Derived tags could implement some custom type conversion etc.
     * <p>
     * This method ignores all property names in the Set returned by {@link #getIgnorePropertySet()}.
     * </p>
     */
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {
        final Map attrsToUse = new HashMap(attributes);
        attrsToUse.keySet().removeAll(getIgnorePropertySet());
        validateBeanProperties(bean, attrsToUse);
        try {
            BeanUtils.populate(bean, attrsToUse);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new JellyTagException("could not set the properties of the bean", e);
        }
    }

    /**
     * If this tag finds an attribute in the XML that's not ignored by {@link #ignoreProperties} and isn't a bean property, should it throw an exception?
     *
     * @param ignoreUnknownProperties Sets {@link #ignoreUnknownProperties}.
     */
    public void setIgnoreUnknownProperties(final boolean ignoreUnknownProperties) {
        this.ignoreUnknownProperties = ignoreUnknownProperties;
    }

    /**
     * If {@link #isIgnoreUnknownProperties()} returns true, make sure that every non-ignored ({@link #addIgnoreProperty(String)}) property matches a writable
     * property on the target bean.
     *
     * @param bean       the bean to validate
     * @param attributes the list of properties to validate
     * @throws JellyTagException when a property is not writeable
     */
    protected void validateBeanProperties(final Object bean, final Map attributes) throws JellyTagException {
        if (!isIgnoreUnknownProperties()) {
            for (final Iterator i = attributes.keySet().iterator(); i.hasNext();) {
                final String attrName = (String) i.next();
                if (!PropertyUtils.isWriteable(bean, attrName)) {
                    throw new JellyTagException("No bean property found: " + attrName);
                }
            }
        }
    }
}
