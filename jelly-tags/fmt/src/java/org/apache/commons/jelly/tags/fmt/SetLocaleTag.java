/*
 * /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/SetLocaleTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
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
 * SetLocaleTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 */
package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.expression.Expression;
import java.util.Locale;

/**
 * Support for tag handlers for &lt;setLocale&gt;, the locale setting
 * tag in JSTL.
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version 1.1
 *
 * @task decide how to implement setResponseLocale
 */
public class SetLocaleTag extends TagSupport {
	
	private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';
	
	private Expression value;
	
	private Expression variant;
	
	private String scope;
	
	/** Creates a new instance of SetLocaleTag */
	public SetLocaleTag() {
	}
	
	/**
	 * Evaluates this tag after all the tags properties have been initialized.
	 *
	 */
	public void doTag(XMLOutput output) throws Exception {
		Locale locale = null;
		
		Object valueInput = null;
		if (this.value != null) {
			valueInput = this.value.evaluate(context);
		}
		Object variantInput = null;
		if (this.variant != null) {
			variantInput = this.variant.evaluate(context);
		}
		
		if (valueInput == null) {
			locale = Locale.getDefault();
		} else if (valueInput instanceof String) {
			if (((String) valueInput).trim().equals("")) {
				locale = Locale.getDefault();
			} else {
				locale = parseLocale((String) valueInput, (String) variantInput);
			}
		} else {
			locale = (Locale) valueInput;
		}
		
		if (scope != null) {
			context.setVariable(Config.FMT_LOCALE, scope, locale);
		}
		else {
			context.setVariable(Config.FMT_LOCALE, locale);
		}
		//setResponseLocale(pageContext, locale);
	}
	
	public void setValue(Expression value) {
		this.value = value;
	}
	
	public void setVariant(Expression variant) {
		this.variant = variant;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	//*********************************************************************
	// Public utility methods
	
	/**
	 * See parseLocale(String, String) for details.
	 */
	public static Locale parseLocale(String locale) {
		return parseLocale(locale, null);
	}
	
	/**
	 * Parses the given locale string into its language and (optionally)
	 * country components, and returns the corresponding
	 * <tt>java.util.Locale</tt> object.
	 *
	 * If the given locale string is null or empty, the runtime's default
	 * locale is returned.
	 *
	 * @param locale the locale string to parse
	 * @param variant the variant
	 *
	 * @return <tt>java.util.Locale</tt> object corresponding to the given
	 * locale string, or the runtime's default locale if the locale string is
	 * null or empty
	 *
	 * @throws IllegalArgumentException if the given locale does not have a
	 * language component or has an empty country component
	 */
	public static Locale parseLocale(String locale, String variant) {
		
		Locale ret = null;
		String language = locale;
		String country = null;
		int index = -1;
		
		if (((index = locale.indexOf(HYPHEN)) > -1)
		|| ((index = locale.indexOf(UNDERSCORE)) > -1)) {
			language = locale.substring(0, index);
			country = locale.substring(index+1);
		}
		
		if ((language == null) || (language.length() == 0)) {
			throw new IllegalArgumentException("Missing language");
		}
		
		if (country == null) {
			if (variant != null)
				ret = new Locale(language, "", variant);
			else
				ret = new Locale(language, "");
		} else if (country.length() > 0) {
			if (variant != null)
				ret = new Locale(language, country, variant);
			else
				ret = new Locale(language, country);
		} else {
			throw new IllegalArgumentException("Missing country");
		}
		
		return ret;
	}
}
