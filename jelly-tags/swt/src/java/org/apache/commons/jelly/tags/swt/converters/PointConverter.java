/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/swt/src/java/org/apache/commons/jelly/tags/swt/converters/PointConverter.java,v 1.2 2003/10/09 21:21:25 rdonkin Exp $
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
 * $Id: PointConverter.java,v 1.2 2003/10/09 21:21:25 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.swt.converters;

import java.util.StringTokenizer;

import org.apache.commons.beanutils.Converter;

import org.eclipse.swt.graphics.Point;

/** 
 * A Converter that turns Strings in the form "x, y" into Point objects
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class PointConverter implements Converter {

	private static final PointConverter instance = new PointConverter();
	 
	public static PointConverter getInstance() {
	    return instance;
	}

	/**
	 * Parsers a String in the form "x, y" into an SWT Point class
	 * @param text
	 * @return Point
	 */
	public Point parse(String text) {
        StringTokenizer enum = new StringTokenizer( text, "," );
        int x = 0;
        int y = 0;
        if ( enum.hasMoreTokens() ) {
            x = parseNumber( enum.nextToken() );
        }
        if ( enum.hasMoreTokens() ) {
            y = parseNumber( enum.nextToken() );
        }           
        return new Point( x, y );
	}
	
	// Converter interface	
    //-------------------------------------------------------------------------
    public Object convert(Class type, Object value) {
        Object answer = null;
        if ( value != null ) {
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