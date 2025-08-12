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
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.SAXException;

/** <p>{@code TextScript} outputs some static text.</p>
  */
public class TextScript implements Script {

    /** The text output by this script */
    private String text;

    public TextScript() {
    }

    public TextScript(final String text) {
        this.text = text;
    }

    // Script interface
    //-------------------------------------------------------------------------
    @Override
    public Script compile() {
        return this;
    }

    /** @return the text output by this script */
    public String getText() {
        return text;
    }

    /** Evaluates the body of a tag */
    @Override
    public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
        if (text != null) {
            try {
                output.write(text);
            } catch (final SAXException e) {
                throw new JellyTagException("could not write to XMLOutput", e);
            }
        }
    }

    /** Sets the text output by this script */
    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return super.toString() + "[text=" + text + "]";
    }

    /**
     * Trims whitespace from the end of the text
     */
    public void trimEndWhitespace() {
        int index = text.length();
        while (--index >= 0) {
            final char ch = text.charAt(index);
            if (!Character.isWhitespace(ch)) {
                break;
            }
        }
        index++;
        if (index < text.length()) {
            this.text = text.substring(0, index);
        }
    }

    /**
     * Trims whitespace from the start of the text
     */
    public void trimStartWhitespace() {
        int index = 0;
        for ( final int length = text.length(); index < length; index++ ) {
            final char ch = text.charAt(index);
            if (!Character.isWhitespace(ch)) {
                break;
            }
        }
        if ( index > 0 ) {
            this.text = text.substring(index);
        }
    }

    /**
     * Trims whitespace from the start and end of the text in this script
     */
    public void trimWhitespace() {
        this.text = text.trim();
    }
}
