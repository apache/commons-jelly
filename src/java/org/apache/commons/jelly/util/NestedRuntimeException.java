/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/util/CommandLineParser.java,v 1.1 2002/10/16 22:58:29 morgand Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/16 22:58:29 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: CommandLineParser.java,v 1.1 2002/10/16 22:58:29 morgand Exp $
 */

package org.apache.commons.jelly.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/** 
 * A {@link RuntimeException} which is nested to preserve stack traces.
 *
 * This class allows the following code to be written to convert a regular
 * Exception into a {@link RuntimeException} without losing the stack trace.
 *
 * <pre>
 *    try {
 *        ...
 *    } catch (Exception e) {
 *        throw new RuntimeException(e);
 *    }
 * </pre>
 *
 * @author James Strachan
 * @version $Revision: 1.2 $
 */

public class NestedRuntimeException extends RuntimeException {

	/**
	 * Holds the reference to the exception or error that caused
	 * this exception to be thrown.
	 */
	private Throwable cause = null;

	/**
	 * Constructs a new <code>NestedRuntimeException</code> with specified
	 * nested <code>Throwable</code>.
	 *
	 * @param cause the exception or error that caused this exception to be
	 * thrown
	 */
	public NestedRuntimeException(Throwable cause) {
		super(cause.getMessage());
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>NestedRuntimeException</code> with specified
	 * detail message and nested <code>Throwable</code>.
	 *
	 * @param msg    the error message
	 * @param cause  the exception or error that caused this exception to be
	 * thrown
	 */
	public NestedRuntimeException(String msg, Throwable cause) {
		super(msg);
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public void printStackTrace() {
		cause.printStackTrace();
	}

	public void printStackTrace(PrintStream out) {
		cause.printStackTrace(out);
	}

	public void printStackTrace(PrintWriter out) {
		cause.printStackTrace(out);
	}

}
