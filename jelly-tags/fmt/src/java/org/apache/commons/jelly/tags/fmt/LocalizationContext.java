/*
 * /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/LocalizationContext.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 * 1.1
 * 2003/01/16 16:21:46
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
 * 
 * LocalizationContext.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 */
package org.apache.commons.jelly.tags.fmt;

import java.util.ResourceBundle;
import java.util.Locale;


/**
 * Class representing an I18N localization context.
 *
 * <p> An I18N localization context has two components: a resource bundle and
 * the locale that led to the resource bundle match.
 *
 * <p> The resource bundle component is used by &lt;fmt:message&gt; for mapping
 * message keys to localized messages, and the locale component is used by the
 * &lt;fmt:message&gt;, &lt;fmt:formatNumber&gt;, &lt;fmt:parseNumber&gt;, &lt;fmt:formatDate&gt;,
 * and &lt;fmt:parseDate&gt; actions as their formatting locale.
 *
 * @see javax.servlet.jsp.jstl.fmt.LocalizationContext
 *
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version 1.1
 */
public class LocalizationContext {
	
	// the localization context's resource bundle
	final private ResourceBundle bundle;
	
	// the localization context's locale
	final private Locale locale;
	
	/**
	 * Constructs an empty I18N localization context.
	 */
	public LocalizationContext() {
		bundle = null;
		locale = null;
	}
	
	/**
	 * Constructs an I18N localization context from the given resource bundle
	 * and locale.
	 *
	 * <p> The specified locale is the application- or browser-based preferred
	 * locale that led to the resource bundle match.
	 *
	 * @param bundle The localization context's resource bundle
	 * @param locale The localization context's locale
	 */
	public LocalizationContext(ResourceBundle bundle, Locale locale) {
		this.bundle = bundle;
		this.locale = locale;
	}
	
	/**
	 * Constructs an I18N localization context from the given resource bundle.
	 *
	 * <p> The localization context's locale is taken from the given
	 * resource bundle.
	 *
	 * @param bundle The resource bundle
	 */
	public LocalizationContext(ResourceBundle bundle) {
		this.bundle = bundle;
		this.locale = bundle.getLocale();
	}
	
	/**
	 * Gets the resource bundle of this I18N localization context.
	 *
	 * @return The resource bundle of this I18N localization context, or null
	 * if this I18N localization context is empty
	 */
	public ResourceBundle getResourceBundle() {
		return bundle;
	}
	
	/**
	 * Gets the locale of this I18N localization context.
	 *
	 * @return The locale of this I18N localization context, or null if this
	 * I18N localization context is empty, or its resource bundle is a
	 * (locale-less) root resource bundle.
	 */
	public Locale getLocale() {
		return locale;
	}
}
