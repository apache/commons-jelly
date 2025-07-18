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

package org.apache.commons.jelly.tags.memory;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Tag supporting displaying free memory.
 */
public class FreeMemoryTag extends TagSupport {

    private static final int MB = 1024 * 1024;
    private static final int KB = 1024;

    private String style = "mb";

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        final Runtime r = Runtime.getRuntime();

        try {
            long total = r.totalMemory();
            long free = total - r.freeMemory();

            if (style.equals("kb")) {
                free /= KB;
                total /= KB;
            }
            else if (style.equals("mb")) {
                free /= MB;
                total /= MB;
            }

            output.write( free + style + "/" + total + style );
        }
        catch ( final Exception e ) {
            throw new JellyTagException( "Error writing to output", e );
        }
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        if (style == null) {
            style = "mb";
        }
        this.style = style.toLowerCase();
    }

}
