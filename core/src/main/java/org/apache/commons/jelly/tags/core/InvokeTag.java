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

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
  * A tag which calls a method in an object instantiated by core:new
  *
  */
public class InvokeTag extends TagSupport implements ArgTagParent {

    /** The variable exported */
    private String var;

    /** The variable where the method's exception is exported */
    private String exceptionVar;

    /** The method to invoke */
    private String methodName;

    /** The object to invoke the method on */
    private Object onInstance;

    private final List paramTypes = new ArrayList();
    private final List paramValues = new ArrayList();

    public InvokeTag() {
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
        if ( null == methodName) {
            throw new MissingAttributeException( "method" );
        }
        if ( null == onInstance ) {
            throw new MissingAttributeException( "on" );
        }

        invokeBody(output);

        final Object[] values = paramValues.toArray();
        final Class[] types = (Class[])paramTypes.toArray(new Class[paramTypes.size()]);

        Object result = null;
        try {
            result = MethodUtils.invokeMethod(onInstance, methodName, values, types);
        }
        catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new JellyTagException(e);
        }
        catch (final InvocationTargetException e) {
            if (null == exceptionVar) {
                throw new JellyTagException("method " + methodName + " threw exception: " + e.getTargetException().getMessage(), e.getTargetException());
            }
            context.setVariable(exceptionVar,e.getTargetException());
        }
        finally {
            paramTypes.clear();
            paramValues.clear();
        }

        final ArgTag parentArg = (ArgTag)findAncestorWithClass(ArgTag.class);
        if (null != parentArg) {
            parentArg.setValue(result);
        }
        if (null != var) {
            context.setVariable(var, result);
        }
    }

    /** Sets the name of a variable that exports the exception thrown by
     * the method's invocation (if any)
     */
    public void setExceptionVar(final String var) {
        this.exceptionVar = var;
    }

    public void setMethod(final String method) {
        this.methodName = method;
    }

    public void setOn(final Object instance) {
        this.onInstance = instance;
    }

    /** Sets the name of the variable exported by this tag */
    public void setVar(final String var) {
        this.var = var;
    }
}
