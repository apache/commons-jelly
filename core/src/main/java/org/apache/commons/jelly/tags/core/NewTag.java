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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils2.ConstructorUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/** A tag which creates a new object of the given type
  */
public class NewTag extends BaseClassLoaderTag implements ArgTagParent {

    /** The variable exported */
    private String var;

    /** The class name of the object to instantiate */
    private String className;

    private final List paramTypes = new ArrayList();
    private final List paramValues = new ArrayList();

    public NewTag() {
    }

    @Override
    public void addArgument(final Class type, final Object value) {
        paramTypes.add(type);
        paramValues.add(value);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        ArgTag parentArg = null;
        if ( var == null ) {
            parentArg = (ArgTag)findAncestorWithClass(ArgTag.class);
            if (null == parentArg) {
                throw new MissingAttributeException( "var" );
            }
        }
        if ( className == null ) {
            throw new MissingAttributeException( "className" );
        }
        invokeBody(output);

        try {
            final Class theClass = getClassLoader().loadClass( className );
            Object object = null;
            if (paramTypes.isEmpty()) {
                object = theClass.getConstructor().newInstance();
            } else {
                final Object[] values = paramValues.toArray();
                final Class[] types = (Class[]) paramTypes.toArray(new Class[paramTypes.size()]);
                object = ConstructorUtils.invokeConstructor(theClass, values, types);
                paramTypes.clear();
                paramValues.clear();
            }
            if (null != var) {
                context.setVariable(var, object);
            } else {
                parentArg.setValue(object);
            }
        }
        catch (final ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JellyTagException(e);
        }
    }

    /** Sets the class name of the object to instantiate */
    public void setClassName(final String className) {
        this.className = className;
    }

    /** Sets the name of the variable exported by this tag */
    public void setVar(final String var) {
        this.var = var;
    }
}
