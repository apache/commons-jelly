/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing.converters;

import javax.swing.DebugGraphics;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;

/** 
 * A Converter that turns Strings in one of the constants of
 *	{@link DebugGraphics} to their appropriate integer constant.
 *
 * @author <a href="mailto:paul@activemath.org">Paul Libbrecht</a>
 * @version $Revision: $
 */
public class DebugGraphicsConverter implements Converter {

    private static String usageText =
        "DebugGraphics options are set as a \"|\" separated list of words using one of the constants of DebugGraphics: log, flash, or buffered.";
        
    public static void register() {
        ConvertUtils.register(
            new DebugGraphicsConverter(),
            java.lang.Integer.class);
    }

    public Object convert(Class type, Object value) {
        if (value != null) {
            int result = 0;
            StringTokenizer stok =
                new StringTokenizer(value.toString(), ", \t|", false);
            while (stok.hasMoreTokens()) {
                String tok = stok.nextToken();
                result = result | recognizeOption(tok);
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
