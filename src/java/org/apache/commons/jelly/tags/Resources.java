/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */ 

package org.apache.commons.jelly.tags;

import java.util.*;
import java.text.*;

/**
 * <p>Provides locale-neutral access to string resources.  Only the
 * documentation and code are in English. :-)
 *
 * <p>The major goal, aside from globalization, is convenience.
 * Access to resources with no parameters is made in the form:</p>
 * <pre>
 *     Resources.getMessage(MESSAGE_NAME);
 * </pre>
 *
 * <p>Access to resources with one parameter works like</p>
 * <pre>
 *     Resources.getMessage(MESSAGE_NAME, arg1);
 * </pre>
 *
 * <p>... and so on.</p>
 *
 * @author Shawn Bayern
 */
public class Resources {

    //*********************************************************************
    // Static data

    /** The location of our resources. */
    private static final String RESOURCE_LOCATION
    = "org.apache.commons.jelly.tags.Resources";

    /** Our class-wide ResourceBundle. */
    private static ResourceBundle rb =
    ResourceBundle.getBundle(RESOURCE_LOCATION);


    //*********************************************************************
    // Public static methods

    /** Retrieves a message with no arguments. */
    public static String getMessage(String name)
        throws MissingResourceException {
    return rb.getString(name);
    }

    /** Retrieves a message with arbitrarily many arguments. */
    public static String getMessage(String name, Object[] a)
        throws MissingResourceException {
    String res = rb.getString(name);
    return MessageFormat.format(res, a);
    }

    /** Retrieves a message with one argument. */
    public static String getMessage(String name, Object a1)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1 });
    }

    /** Retrieves a message with two arguments. */
    public static String getMessage(String name, Object a1, Object a2)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1, a2 });
    }

    /** Retrieves a message with three arguments. */
    public static String getMessage(String name,
                    Object a1,
                    Object a2,
                    Object a3)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1, a2, a3 });
    }

    /** Retrieves a message with four arguments. */
    public static String getMessage(String name,
                    Object a1,
                    Object a2,
                    Object a3,
                    Object a4)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1, a2, a3, a4 });
    }

    /** Retrieves a message with five arguments. */
    public static String getMessage(String name,
                    Object a1,
                    Object a2,
                    Object a3,
                    Object a4,
                    Object a5)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1, a2, a3, a4, a5 });
    }

    /** Retrieves a message with six arguments. */
    public static String getMessage(String name,
                    Object a1,
                    Object a2,
                    Object a3,
                    Object a4,
                    Object a5,
                    Object a6)
        throws MissingResourceException {
    return getMessage(name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

}
