/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/SwtHelper.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
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
 * SwtHelper.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * A helper class for working with SWT.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class SwtHelper extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SwtHelper.class);
    

    /**
     * Parses the comma delimited String of style codes which are or'd
     * together. The given class describes the integer static constants
     *
     * @param constantClass is the type to look for static fields
     * @param text is a comma delimited text value such as "border, resize"
     * @return the int code
     */
    public static int parseStyle(Class constantClass, String text) throws JellyTagException {
        return parseStyle(constantClass, text, true);
    }

    /**
     * Parses the comma delimited String of style codes which are or'd
     * together. The given class describes the integer static constants
     *
     * @param constantClass is the type to look for static fields
     * @param text is a comma delimited text value such as "border, resize"
     * @param toUpperCase is whether the text should be converted to upper case
     * before its compared against the reflection fields
     * 
     * @return the int code
     */
    public static int parseStyle(Class constantClass, String text, boolean toUpperCase) throws JellyTagException{
        int answer = 0;
        if (text != null) {
            if (toUpperCase) {
                text = text.toUpperCase();
            }
            StringTokenizer enum = new StringTokenizer(text, ",");
            while (enum.hasMoreTokens()) {
                String token = enum.nextToken().trim();
                answer |= getStyleCode(constantClass, token);
            }
        }
        return answer;
    }
    
    /**
     * @return the code for the given word or zero if the word doesn't match a
     * valid style
     */
    public static int getStyleCode(Class constantClass,String text) throws JellyTagException {
        try {
            Field field = constantClass.getField(text);
            if (field == null) {
                log.warn( "Unknown style code: " + text +" will be ignored");
                return 0;
            }
            return field.getInt(null);
        } catch (NoSuchFieldException e) {
            throw new JellyTagException("The value: " + text + " is not understood", e);
        } catch (IllegalAccessException e) {
            throw new JellyTagException("The value: " + text + " is not understood", e);
        }
    }
}
