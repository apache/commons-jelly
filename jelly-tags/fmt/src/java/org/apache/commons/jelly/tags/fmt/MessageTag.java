/*
 * /home/cvs/jakarta-commons-sandbox/jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/MessageTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
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
 * MessageTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 */
package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.expression.Expression;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 * Support for tag handlers for &lt;message&gt;, the lookup up 
 * localized message tag in JSTL.
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version 1.1
 *
 * @task decide how to implement setResponseLocale
 */
public class MessageTag extends TagSupport {
	
	private static final String UNDEFINED_KEY = "???";
	
	private Expression key;
	private Expression bundle;
	
	private LocalizationContext locCtxt;
	
	private String var;
	
	private String scope;
	
	private List params;
	
	/** Creates a new instance of MessageTag */
	public MessageTag() {
		params = new ArrayList();
	}
	
	/**
	 * Adds an argument (for parametric replacement) to this tag's message.
	 */
	public void addParam(Object arg) {
		params.add(arg);
	}
	
	public void doTag(XMLOutput output) throws Exception {
		Object keyInput = null;
		if (this.key != null) {
			keyInput = this.key.evaluate(context);
		}
		else {
			// get key from body
			keyInput = getBodyText();
		}
		
		if ((keyInput == null) || keyInput.equals("")) {
			output.write("??????");
			return;
		}
		
		Object bundleInput = null;
		if (this.bundle != null) {
			bundleInput = this.bundle.evaluate(context);
		}
		if (bundleInput != null && bundleInput instanceof LocalizationContext) {
			locCtxt = (LocalizationContext) bundleInput;
		}
		
		String prefix = null;
		if (locCtxt == null) {
			Tag t = findAncestorWithClass(this, BundleTag.class);
			if (t != null) {
				// use resource bundle from parent <bundle> tag
				BundleTag parent = (BundleTag) t;
				locCtxt = parent.getLocalizationContext();
				prefix = parent.getPrefixAsString();
			} else {
				locCtxt = BundleTag.getLocalizationContext(context);
			}
		} else {
			// localization context taken from 'bundle' attribute
			if (locCtxt.getLocale() != null) {
				// TODO
				// SetLocaleSupport.setResponseLocale(pageContext,
				// locCtxt.getLocale());
			}
		}
		
		String message = UNDEFINED_KEY + keyInput + UNDEFINED_KEY;
		if (locCtxt != null) {
			ResourceBundle bundle = locCtxt.getResourceBundle();
			if (bundle != null) {
				try {
					// prepend 'prefix' attribute from parent bundle
					if (prefix != null) {
						keyInput = prefix + keyInput;
					}
					message = bundle.getString(keyInput.toString());
					// Perform parametric replacement if required
					if (!params.isEmpty()) {
						Object[] messageArgs = params.toArray();
						MessageFormat formatter = new MessageFormat("");
						if (locCtxt.getLocale() != null) {
							formatter.setLocale(locCtxt.getLocale());
						}
						formatter.applyPattern(message);
						message = formatter.format(messageArgs);
					}
				} catch (MissingResourceException mre) {
					message = UNDEFINED_KEY + keyInput + UNDEFINED_KEY;
				}
			}
		}
		
		
		if (scope != null) {
			if (var != null) {
				context.setVariable(var, scope, message);
			}
			else {
				throw new JellyException( "If 'scope' is specified, 'var' must be defined for this tag" );
			}
		}
		else {
			if (var != null) {
				context.setVariable(var, message);
			}
			else {
				// write the message
				output.write(message);
			}
		}
	}
	
	/** Setter for property key.
	 * @param key New value of property key.
	 *
	 */
	public void setKey(Expression key) {
		this.key = key;
	}
	
	/** Setter for property bundle.
	 * @param bundle New value of property bundle.
	 *
	 */
	public void setBundle(Expression bundle) {
		this.bundle = bundle;
	}
	
	/** Setter for property var.
	 * @param var New value of property var.
	 *
	 */
	public void setVar(String var) {
		this.var = var;
	}
	
	/** Setter for property scope.
	 * @param scope New value of property scope.
	 *
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
}
