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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;

/**
  * A Tag which can invoke a static method on a class, without an
  * instance of the class being needed.
  * <p>
  * Like the {@link InvokeTag}, this tag can take a set of
  * arguments using the {@link ArgTag}.
  * </p>
  * <p>
  * The following attributes are required:
  * </p>
  * <ul>
  *   <li>var - The variable to assign the return of the method call to</li>
  *   <li>method - The name of the static method to invoke</li>
  *   <li>className - The name of the class containing the static method</li>
  * </ul>
  */
public class InvokeStaticTag extends TagSupport implements ArgTagParent {

    /** The variable exported */
    private String var;

    /** The variable where the method's exception is exported */
    private String exceptionVar;

    /** The method to invoke */
    private String methodName;

    /** The object to invoke the method on */
    private String className;

    private final List paramTypes = new ArrayList();
    private final List paramValues = new ArrayList();

    public InvokeStaticTag() {
    }

    /**
     * Adds an argument to supply to the method
     *
     * @param type The Class type of the argument
     * @param value The value of the argument
     */
    @Override
    public void addArgument(final Class type, final Object value) {
        paramTypes.add(type);
        paramValues.add(value);
    }

    /**
     * Factory method to create a new JellyTagException instance from a given
     * failure exception
     * @param e is the exception which occurred attempting to load the class
     * @return JellyTagException
     */
    protected JellyTagException createLoadClassFailedException(final Exception e) {
        return new JellyTagException(
            "Could not load class: " + className + ". Reason: " + e, e
        );
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            if ( null == methodName) {
                throw new MissingAttributeException( "method" );
            }
            invokeBody(output);

            final Object[] values = paramValues.toArray();
            final Class[] types = (Class[])paramTypes.toArray(new Class[paramTypes.size()]);
            final Method method = loadClass().getMethod( methodName, types );
            final Object result = method.invoke( null, values );
            if (null != var) {
                context.setVariable(var, result);
            }

            final ArgTag parentArg = (ArgTag)findAncestorWithClass(ArgTag.class);
            if (null != parentArg) {
                parentArg.setValue(result);
            }
        }
        catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            throw createLoadClassFailedException(e);
        }
        catch (final InvocationTargetException e) {
            if (null == exceptionVar) {
                throw new JellyTagException("method " + methodName +
                    " threw exception: "+ e.getTargetException().getMessage(),
                    e.getTargetException() );
            }
            context.setVariable(exceptionVar, e.getTargetException());
        }
        finally {
            paramTypes.clear();
            paramValues.clear();
        }
    }

    /**
     * Loads the class using either the class loader which loaded me or the
     * current threads context class loader
     */
    protected Class loadClass() throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(className, getClass());
    }

    /**
     * Sets the fully qualified class name containing the static method
     *
     * @param className The name of the class
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /** Sets the name of a variable that exports the exception thrown by
     * the method's invocation (if any)
     */
    public void setExceptionVar(final String var) {
        this.exceptionVar = var;
    }

    // Tag interface
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the method to invoke
     *
     * @param methodName The method name
     */
    public void setMethod(final String methodName) {
        this.methodName = methodName;
    }

    /**
     * Sets the name of the variable exported by this tag
     *
     * @param var The variable name
     */
    public void setVar(final String var) {
        this.var = var;
    }
}

