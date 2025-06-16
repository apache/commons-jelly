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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Creates a nested property via calling a beans createFoo() method then
 * either calling the setFoo(value) or addFoo(value) methods in a similar way
 * to how Ant tags construct themselves.
 */
public class BeanPropertyTag extends BeanTag {

    /** Empty arguments constant */
    private static final Object[] EMPTY_ARGS = {};

    /** Empty argument types constant */
    private static final Class[] EMPTY_ARG_TYPES = {};

    /** The name of the create method */
    private String createMethodName;

    public BeanPropertyTag(final String tagName) {
        super(Object.class, tagName);

        if (tagName.length() > 0) {
            createMethodName = "create"
                + tagName.substring(0,1).toUpperCase()
                + tagName.substring(1);
        }
    }

    /**
     * finds the parameter type of the first public method in the parent class whose name
     * matches the add{tag name} pattern, whose return type is void and which takes
     * one argument only.
     * @param parentClass
     * @return the class of the first and only parameter
     */
    protected Class findAddMethodClass(final Class parentClass) {
        final Method[] methods = parentClass.getMethods();
        for (final Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                final Class[] args = method.getParameterTypes();
                if (method.getName().equals(addMethodName)
                      && java.lang.Void.TYPE.equals(method.getReturnType())
                      && args.length == 1
                      && !java.lang.String.class.equals(args[0])
                      && !args[0].isArray()
                      && !args[0].isPrimitive()) {
                    return args[0];
                }
            }
        }
        return null;
    }

    /**
     * Finds the Method to create a new property object
     */
    protected Method findCreateMethod(final Class theClass) {
        if (createMethodName == null) {
            return null;
        }
        return MethodUtils.getAccessibleMethod(
            theClass, createMethodName, EMPTY_ARG_TYPES
        );
    }

    /**
     * Creates a new instance by calling a create method on the parent bean
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output) throws JellyTagException {
        final Object parentObject = getParentObject();
        if (parentObject != null) {
            // now lets try call the create method...
            final Class parentClass = parentObject.getClass();
            final Method method = findCreateMethod(parentClass);
            if (method != null) {
                try {
                    return method.invoke(parentObject, EMPTY_ARGS);
                }
                catch (final Exception e) {
                    throw new JellyTagException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                }
            }
            Class tagClass = theClass;
            if (tagClass == Object.class) {
                tagClass = findAddMethodClass(parentClass);
            }
            if (tagClass == null) {
                throw new JellyTagException("unable to infer element class for tag "+getTagName());
            }

            return super.newInstance(tagClass, attributes, output) ;
        }
        throw new JellyTagException("The " + getTagName() + " tag must be nested within a tag which maps to a BeanSource implementor");
    }
}
