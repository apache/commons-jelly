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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */

package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyContext;


/**
 * Class supporting access to configuration data.
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version 1.1
 */
public class Config {
	
	/*
	 * I18N/Formatting actions related configuration data
	 */
	
	/**
	 * Name of configuration setting for application- (as opposed to browser-)
	 * based preferred locale
	 */
	public static final String FMT_LOCALE
	= "org.apache.commons.jelly.tags.fmt.locale";
	
	/**
	 * Name of configuration setting for fallback locale
	 */
	public static final String FMT_FALLBACK_LOCALE
	= "org.apache.commons.jelly.tags.fmt.fallbackLocale";
	
	/**
	 * Name of configuration setting for i18n localization context
	 */
	public static final String FMT_LOCALIZATION_CONTEXT
	= "org.apache.commons.jelly.tags.fmt.localizationContext";
	
	/**
	 * Name of localization setting for time zone
	 */
	public static final String FMT_TIME_ZONE
	= "org.apache.commons.jelly.tags.fmt.timeZone";
	
	
	/**
	 * Looks up a configuration variable in the given scope.
	 *
	 * <p> The lookup of configuration variables is performed as if each scope
	 * had its own name space, that is, the same configuration variable name
	 * in one scope does not replace one stored in a different scope.
	 *
	 * @param jc Page context in which the configuration variable is to be
	 * looked up
	 * @param name Configuration variable name
	 * @param scope Scope in which the configuration variable is to be looked
	 * up
	 *
	 * @return The <tt>java.lang.Object</tt> associated with the configuration
	 * variable, or null if it is not defined.
	 */
//	public static Object get(JellyContext jc, String name, int scope) {
//		switch (scope) {
//			case JellyContext.PAGE_SCOPE:
//				return jc.getAttribute(name + PAGE_SCOPE_SUFFIX, scope);
//			case JellyContext.REQUEST_SCOPE:
//				return jc.getAttribute(name + REQUEST_SCOPE_SUFFIX, scope);
//			case JellyContext.SESSION_SCOPE:
//				return jc.getAttribute(name + SESSION_SCOPE_SUFFIX, scope);
//			case JellyContext.APPLICATION_SCOPE:
//				return jc.getAttribute(name + APPLICATION_SCOPE_SUFFIX, scope);
//			default:
//				throw new IllegalArgumentException("unknown scope");
//		}
//	}
	
	/**
	 * Looks up a configuration variable in the "request" scope.
	 *
	 * <p> The lookup of configuration variables is performed as if each scope
	 * had its own name space, that is, the same configuration variable name
	 * in one scope does not replace one stored in a different scope.
	 *
	 * @param request Request object in which the configuration variable is to
	 * be looked up
	 * @param name Configuration variable name
	 *
	 * @return The <tt>java.lang.Object</tt> associated with the configuration
	 * variable, or null if it is not defined.
	 */
//	public static Object get(ServletRequest request, String name) {
//		return request.getAttribute(name + REQUEST_SCOPE_SUFFIX);
//	}
	
	/**
	 * Looks up a configuration variable in the "session" scope.
	 *
	 * <p> The lookup of configuration variables is performed as if each scope
	 * had its own name space, that is, the same configuration variable name
	 * in one scope does not replace one stored in a different scope.
	 *
	 * @param session Session object in which the configuration variable is to
	 * be looked up
	 * @param name Configuration variable name
	 *
	 * @return The <tt>java.lang.Object</tt> associated with the configuration
	 * variable, or null if it is not defined.
	 */
//	public static Object get(HttpSession session, String name) {
//		return session.getAttribute(name + SESSION_SCOPE_SUFFIX);
//	}
	
	/**
	 * Looks up a configuration variable in the "application" scope.
	 *
	 * <p> The lookup of configuration variables is performed as if each scope
	 * had its own name space, that is, the same configuration variable name
	 * in one scope does not replace one stored in a different scope.
	 *
	 * @param context Servlet context in which the configuration variable is
	 * to be looked up
	 * @param name Configuration variable name
	 *
	 * @return The <tt>java.lang.Object</tt> associated with the configuration
	 * variable, or null if it is not defined.
	 */
//	public static Object get(ServletContext context, String name) {
//		return context.getAttribute(name + APPLICATION_SCOPE_SUFFIX);
//	}
	
	/**
	 * Sets the value of a configuration variable in the given scope.
	 *
	 * <p> Setting the value of a configuration variable is performed as if
	 * each scope had its own namespace, that is, the same configuration
	 * variable name in one scope does not replace one stored in a different
	 * scope.
	 *
	 * @param jc Page context in which the configuration variable is to be set
	 * @param name Configuration variable name
	 * @param value Configuration variable value
	 * @param scope Scope in which the configuration variable is to be set
	 */
//	public static void set(JellyContext jc, String name, Object value,
//	int scope) {
//		switch (scope) {
//			case JellyContext.PAGE_SCOPE:
//				jc.setAttribute(name + PAGE_SCOPE_SUFFIX, value, scope);
//				break;
//			case JellyContext.REQUEST_SCOPE:
//				jc.setAttribute(name + REQUEST_SCOPE_SUFFIX, value, scope);
//				break;
//			case JellyContext.SESSION_SCOPE:
//				jc.setAttribute(name + SESSION_SCOPE_SUFFIX, value, scope);
//				break;
//			case JellyContext.APPLICATION_SCOPE:
//				jc.setAttribute(name + APPLICATION_SCOPE_SUFFIX, value, scope);
//				break;
//			default:
//				throw new IllegalArgumentException("unknown scope");
//		}
//	}
	
	/**
	 * Sets the value of a configuration variable in the "request" scope.
	 *
	 * <p> Setting the value of a configuration variable is performed as if
	 * each scope had its own namespace, that is, the same configuration
	 * variable name in one scope does not replace one stored in a different
	 * scope.
	 *
	 * @param request Request object in which the configuration variable is to
	 * be set
	 * @param name Configuration variable name
	 * @param value Configuration variable value
	 */
//	public static void set(ServletRequest request, String name, Object value) {
//		request.setAttribute(name + REQUEST_SCOPE_SUFFIX, value);
//	}
	
	/**
	 * Sets the value of a configuration variable in the "session" scope.
	 *
	 * <p> Setting the value of a configuration variable is performed as if
	 * each scope had its own namespace, that is, the same configuration
	 * variable name in one scope does not replace one stored in a different
	 * scope.
	 *
	 * @param session Session object in which the configuration variable is to
	 * be set
	 * @param name Configuration variable name
	 * @param value Configuration variable value
	 */
//	public static void set(HttpSession session, String name, Object value) {
//		session.setAttribute(name + SESSION_SCOPE_SUFFIX, value);
//	}
	
	/**
	 * Sets the value of a configuration variable in the "application" scope.
	 *
	 * <p> Setting the value of a configuration variable is performed as if
	 * each scope had its own namespace, that is, the same configuration
	 * variable name in one scope does not replace one stored in a different
	 * scope.
	 *
	 * @param context Servlet context in which the configuration variable is to
	 * be set
	 * @param name Configuration variable name
	 * @param value Configuration variable value
	 */
//	public static void set(ServletContext context, String name, Object value) {
//		context.setAttribute(name + APPLICATION_SCOPE_SUFFIX, value);
//	}
	
	/**
	 * Removes a configuration variable from the given scope.
	 *
	 * <p> Removing a configuration variable is performed as if each scope had
	 * its own namespace, that is, the same configuration variable name in one
	 * scope does not impact one stored in a different scope.
	 *
	 * @param jc Page context from which the configuration variable is to be
	 * removed
	 * @param name Configuration variable name
	 * @param scope Scope from which the configuration variable is to be
	 * removed
	 */
//	public static void remove(JellyContext jc, String name, int scope) {
//		switch (scope) {
//			case JellyContext.PAGE_SCOPE:
//				jc.removeAttribute(name + PAGE_SCOPE_SUFFIX, scope);
//				break;
//			case JellyContext.REQUEST_SCOPE:
//				jc.removeAttribute(name + REQUEST_SCOPE_SUFFIX, scope);
//				break;
//			case JellyContext.SESSION_SCOPE:
//				jc.removeAttribute(name + SESSION_SCOPE_SUFFIX, scope);
//				break;
//			case JellyContext.APPLICATION_SCOPE:
//				jc.removeAttribute(name + APPLICATION_SCOPE_SUFFIX, scope);
//				break;
//			default:
//				throw new IllegalArgumentException("unknown scope");
//		}
//	}
	
	/**
	 * Removes a configuration variable from the "request" scope.
	 *
	 * <p> Removing a configuration variable is performed as if each scope had
	 * its own namespace, that is, the same configuration variable name in one
	 * scope does not impact one stored in a different scope.
	 *
	 * @param request Request object from which the configuration variable is
	 * to be removed
	 * @param name Configuration variable name
	 */
//	public static void remove(ServletRequest request, String name) {
//		request.removeAttribute(name + REQUEST_SCOPE_SUFFIX);
//	}
	
	/**
	 * Removes a configuration variable from the "session" scope.
	 *
	 * <p> Removing a configuration variable is performed as if each scope had
	 * its own namespace, that is, the same configuration variable name in one
	 * scope does not impact one stored in a different scope.
	 *
	 * @param session Session object from which the configuration variable is
	 * to be removed
	 * @param name Configuration variable name
	 */
//	public static void remove(HttpSession session, String name) {
//		session.removeAttribute(name + SESSION_SCOPE_SUFFIX);
//	}
	
	/**
	 * Removes a configuration variable from the "application" scope.
	 *
	 * <p> Removing a configuration variable is performed as if each scope had
	 * its own namespace, that is, the same configuration variable name in one
	 * scope does not impact one stored in a different scope.
	 *
	 * @param context Servlet context from which the configuration variable is
	 * to be removed
	 * @param name Configuration variable name
	 */
//	public static void remove(ServletContext context, String name) {
//		context.removeAttribute(name + APPLICATION_SCOPE_SUFFIX);
//	}
	
	/**
	 * Finds the value associated with a specific configuration setting
	 * identified by its context initialization parameter name.
	 *
	 * <p> For each of the JSP scopes (page, request, session, application),
	 * get the value of the configuration variable identified by <tt>name</tt>
	 * using method <tt>get()</tt>. Return as soon as a non-null value is
	 * found. If no value is found, get the value of the context initialization
	 * parameter identified by <tt>name</tt>.
	 *
	 * @param jc Page context in which the configuration setting is to be
	 * searched
	 * @param name Context initialization parameter name of the configuration
	 * setting
	 *
	 * @return The <tt>java.lang.Object</tt> associated with the configuration
	 * setting identified by <tt>name</tt>, or null if it is not defined.
	 */
//	public static Object find(JellyContext jc, String name) {
//		Object ret = jc.getVariable(name, JellyContext.PAGE_SCOPE);
//		if (ret == null) {
//			ret = get(jc, name, JellyContext.REQUEST_SCOPE);
//			if (ret == null) {
//				if (jc.getSession() != null) {
//					// check session only if a session is present
//					ret = get(jc, name, JellyContext.SESSION_SCOPE);
//				}
//				if (ret == null) {
//					ret = get(jc, name, JellyContext.APPLICATION_SCOPE);
//					if (ret == null) {
//						ret = jc.getServletContext().getInitParameter(name);
//					}
//				}
//			}
//		}
//		
//		return ret;
//	}
}
