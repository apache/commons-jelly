/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/swt/src/java/org/apache/commons/jelly/tags/swt/converters/ColorConverter.java,v 1.2 2003/10/09 21:21:25 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/09 21:21:25 $
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
 * $Id: ColorConverter.java,v 1.2 2003/10/09 21:21:25 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.swt.converters;

import java.util.StringTokenizer;

import org.apache.commons.beanutils.Converter;
import org.eclipse.swt.graphics.RGB;

/** 
 * A Converter that converts Strings in the form "#uuuuuu" or "x,y,z" into a RGB object 
 *
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a>
 * @version $Revision: 1.2 $
 */
public class ColorConverter implements Converter {

	private static final ColorConverter instance = new ColorConverter();

	private static String usageText =
		"Color value should be in the form of '#xxxxxx' or 'x,y,z'";

	public static ColorConverter getInstance() {
		return instance;
	}

	/**
	 * Parsers a String in the form "x, y, z" into an SWT RGB class
	 * @param value
	 * @return RGB
	 */
	protected RGB parseRGB(String value) {
		StringTokenizer enum = new StringTokenizer(value, ",");
		int red = 0;
		int green = 0;
		int blue = 0;
		if (enum.hasMoreTokens()) {
			red = parseNumber(enum.nextToken());
		}
		if (enum.hasMoreTokens()) {
			green = parseNumber(enum.nextToken());
		}
		if (enum.hasMoreTokens()) {
			blue = parseNumber(enum.nextToken());
		}
		return new RGB(red, green, blue);
	}

	/**
	 * Parsers a String in the form "#xxxxxx" into an SWT RGB class
	 * @param value
	 * @return RGB
	 */
	protected RGB parseHtml(String value) {
		if (value.length() != 7) {
			throw new IllegalArgumentException(usageText);
		}
		int colorValue = 0;
		try {
			colorValue = Integer.parseInt(value.substring(1), 16);
			java.awt.Color swingColor = new java.awt.Color(colorValue);
			return new RGB(
				swingColor.getRed(),
				swingColor.getGreen(),
				swingColor.getBlue());
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
				value + "is not a valid Html color\n " + ex);
		}
	}

	/**
	 * Parse a String
	 */
	public RGB parse(String value) {
		if (value.length() <= 1) {
			throw new IllegalArgumentException(usageText);
		}

		if (value.charAt(0) == '#') {
			return parseHtml(value);
		} else if (value.indexOf(',') != -1) {
			return parseRGB(value);
		} else {
			throw new IllegalArgumentException(usageText);
		}
	}

	// Converter interface	
	//-------------------------------------------------------------------------
	public Object convert(Class type, Object value) {
		Object answer = null;
		if (value != null) {
			String text = value.toString();
			answer = parse(text);
		}

		System.out.println("Converting value: " + value + " into: " + answer);

		return answer;
	}

	protected int parseNumber(String text) {
		text = text.trim();
		return Integer.parseInt(text.trim());
	}
}
