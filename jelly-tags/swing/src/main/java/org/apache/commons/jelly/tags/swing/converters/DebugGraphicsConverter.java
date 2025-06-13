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
package org.apache.commons.jelly.tags.swing.converters;

import java.util.StringTokenizer;

import javax.swing.DebugGraphics;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.Converter;

/**
 * A Converter that turns Strings in one of the constants of
 *    {@link DebugGraphics} to their appropriate integer constant.
 */
public class DebugGraphicsConverter implements Converter {

    private static String usageText =
        "DebugGraphics options are set as a \"|\" separated list of words using one of the constants of DebugGraphics: log, flash, or buffered.";

    public static void register() {
        ConvertUtils.register(
            new DebugGraphicsConverter(),
            java.lang.Integer.class);
    }

    /** Part of the Converter interface.
     * @see org.apache.commons.beanutils2.Converter#convert(java.lang.Class, java.lang.Object)
     */
    @Override
    public Object convert(final Class type, final Object value) {
        return convert(value);
    }

    /** This is not part of the converter interface, it's for use by
     * classes that don't use DebugGraphicsConverter through BeanUtils.
     * @param value
     * @return
     */
    public Object convert(final Object value) {
        if (value != null) {
            int result = 0;
            final StringTokenizer stok =
                new StringTokenizer(value.toString(), ", \t|", false);
            while (stok.hasMoreTokens()) {
                final String tok = stok.nextToken();
                result |= recognizeOption(tok);
            }
            return new Integer(result);
        }
        return null;
    }

    protected int recognizeOption(String value) {
        value = value.toString().toLowerCase();

        if (value == null) {
            throw new IllegalArgumentException(usageText);
        }
        switch (value) {
        case "log":
        case "log_option":
            return DebugGraphics.LOG_OPTION;
        case "flash":
        case "flash_option":
            return DebugGraphics.FLASH_OPTION;
        case "buffered":
        case "buffered_option":
            return DebugGraphics.BUFFERED_OPTION;
        default:
            throw new IllegalArgumentException(usageText);
        }
    }
}
