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

import javax.swing.DebugGraphics;
import java.util.StringTokenizer;

import org.apache.commons.beanutils2.Converter;
import org.apache.commons.beanutils2.ConvertUtils;

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
    public Object convert(Class type, Object value) {
        return convert(value);
    }
    
    /** This is not part of the converter interface, it's for use by
     * classes that don't use DebugGraphicsConverter through BeanUtils.
     * @param value
     * @return
     */
    public Object convert(Object value) {
        if (value != null) {
            int result = 0;
            StringTokenizer stok =
                new StringTokenizer(value.toString(), ", \t|", false);
            while (stok.hasMoreTokens()) {
                String tok = stok.nextToken();
                result |= recognizeOption(tok);
            }
            return new Integer(result);
        }
        return null;
    }

    protected int recognizeOption(String value) {
        value = value.toString().toLowerCase();

        if ("log".equals(value) || "log_option".equals(value)) {
            return DebugGraphics.LOG_OPTION;
        }
        else if ("flash".equals(value) || "flash_option".equals(value)) {
                return DebugGraphics.FLASH_OPTION;
        }
        else if ("buffered".equals(value) || "buffered_option".equals(value)) {
            return DebugGraphics.BUFFERED_OPTION;
        }
        else {
            throw new IllegalArgumentException(usageText);
        }
    }
}
