/*
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
 */
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runs its body and asserts that an exception is thrown by it.  If no
 * exception is thrown the tag fails.  By default all exceptions are caught.
 * If however <code>expected</code> was specified the body must throw
 * an exception of the given class, otherwise the assertion fails.  The
 * exception thrown by the body can also be of any subtype of the specified
 * exception class.  The optional <code>var</code> attribute can be specified if
 * the caught exception is to be exported to a variable.
 */
public class AssertThrownTag extends AssertTagSupport {

	/** The Log to which logging calls will be made. */
	private static final Log log = LogFactory.getLog(AssertThrownTag.class);

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
     * Sets the ClassLoader to be used when loading an exception class     */
    private ClassLoader classLoader;
    
	// Tag interface
	//-------------------------------------------------------------------------
	public void doTag(XMLOutput output) throws Exception {
		Class throwableClass = getThrowableClass();

		try {
			invokeBody(output);
		} 
        catch (Throwable t) {
            if (t instanceof JellyException) {
                // unwrap Jelly exceptions which wrap other exceptions
                JellyException je = (JellyException) t;
                if (je.getCause() != null) {
                    t = je.getCause();
                }
            }
			if (var != null) {
				context.setVariable(var, t);
			}
			if (throwableClass != null && !throwableClass.isAssignableFrom(t.getClass())) {
				fail("Unexpected exception: " + t);
			} 
            else {
				return;
			}
		}
		fail("No exception was thrown.");
	}

	// Properties
	//-------------------------------------------------------------------------
	/**
	 * Sets the class name of exception expected to be thrown by the body.  The
	 * class name must be fully qualified and can either be the expected
	 * exception class itself or any supertype of it, but must be a subtype of
	 * <code>java.lang.Throwable</code>.
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}

	/**
	 * Sets the variable name to define for this expression.
	 */
	public void setVar(String var) {
		this.var = var;
	}

    /**
     * Sets the class loader to be used to load the exception type     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return getClass().getClassLoader();
        }
        return classLoader;
    }
    
	// Implementation methods
	//-------------------------------------------------------------------------

	/**
	 * Returns the <code>Class</code> corresponding to the class
	 * specified by <code>expected</code>. If
	 * <code>expected</code> was either not specified then <code>java. lang.
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
        catch (ClassNotFoundException e) {
            try {
                throwableClass = Thread.currentThread().getContextClassLoader().loadClass(expected);
            }
            catch (ClassNotFoundException e2) {
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
}
