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
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runs its body and asserts that an exception is thrown by it.  If no
 * exception is thrown the tag fails.  By default all exceptions are caught.
 * If however {@code expected} was specified the body must throw
 * an exception of the given class, otherwise the assertion fails.  The
 * exception thrown by the body can also be of any subtype of the specified
 * exception class.  The optional {@code var} attribute can be specified if
 * the caught exception is to be exported to a variable.
 */
public class AssertThrowsTag extends AssertTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AssertThrowsTag.class);

    /**
     * The variable name to export the caught exception to.
     */
    private String var;

    /**
     * The class name (fully qualified) of the exception expected to be thrown
     * by the body.  Also a superclass of the expected exception can be given.
     */
    private String expected;

    /**
     * Sets the ClassLoader to be used when loading an exception class
     */
    private ClassLoader classLoader;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        Class throwableClass = null;
        try {
            throwableClass = getThrowableClass();
            invokeBody(output);
        }
        catch (Throwable t) {
            if (t instanceof JellyException) {
                // unwrap Jelly exceptions which wrap other exceptions
                final JellyException je = (JellyException) t;
                if (je.getCause() != null) {
                    t = je.getCause();
                }
            }
            if (var != null) {
                context.setVariable(var, t);
            }
            if (throwableClass == null || throwableClass.isAssignableFrom(t.getClass())) {
                return;
            }
            fail("Unexpected exception: " + t);
        }
        fail("No exception was thrown.");
    }

    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, getClass());
    }

    /**
     * Returns the {@code Class} corresponding to the class
     * specified by {@code expected}. If
     * {@code expected} was either not specified then <code>java. lang.
     * Throwable</code> is returned.
     * Otherwise if the class couldn't be
     * found or doesn't denote an exception class then an exception is thrown.
     *
     * @return Class The class of the exception to expect
     */
    protected Class getThrowableClass() throws ClassNotFoundException {
        if (expected == null) {
            return Throwable.class;
        }

        Class throwableClass = null;
        try {
            throwableClass = getClassLoader().loadClass(expected);
        }
        catch (final ClassNotFoundException e) {
            try {
                throwableClass = Thread.currentThread().getContextClassLoader().loadClass(expected);
            }
            catch (final ClassNotFoundException e2) {
                log.warn( "Could not find exception class: " + expected );
                throw e;
            }
        }

        if (!Throwable.class.isAssignableFrom(throwableClass)) {
            log.warn( "The class: " + expected + " is not an Exception class.");
            return null;
        }
        return throwableClass;
    }

    /**
     * Sets the class loader to be used to load the exception type
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Sets the class name of exception expected to be thrown by the body.  The
     * class name must be fully qualified and can either be the expected
     * exception class itself or any supertype of it, but must be a subtype of
     * {@code java.lang.Throwable}.
     */
    public void setExpected(final String expected) {
        this.expected = expected;
    }

    /**
     * Sets the variable name to define for this expression.
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
