/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version 1.1
 */
public class SwtHelper extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SwtHelper.class);

    /**
     * @return the code for the given word or zero if the word doesn't match a
     * valid style
     */
    public static int getStyleCode(final Class constantClass,final String text) throws JellyTagException {
        try {
            final Field field = constantClass.getField(text);
            if (field == null) {
                log.warn( "Unknown style code: " + text +" will be ignored");
                return 0;
            }
            return field.getInt(null);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new JellyTagException("The value: " + text + " is not understood", e);
        }
    }

    /**
     * Parses the comma delimited String of style codes which are or'd
     * together. The given class describes the integer static constants
     *
     * @param constantClass is the type to look for static fields
     * @param text is a comma delimited text value such as "border, resize"
     * @return the int code
     */
    public static int parseStyle(final Class constantClass, final String text) throws JellyTagException {
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
    public static int parseStyle(final Class constantClass, String text, final boolean toUpperCase) throws JellyTagException{
        int answer = 0;
        if (text != null) {
            if (toUpperCase) {
                text = text.toUpperCase();
            }
            final StringTokenizer items = new StringTokenizer(text, ",");
            while (items.hasMoreTokens()) {
                final String token = items.nextToken().trim();
                answer |= getStyleCode(constantClass, token);
            }
        }
        return answer;
    }
}
