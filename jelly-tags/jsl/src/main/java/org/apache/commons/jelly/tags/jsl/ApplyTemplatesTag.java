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
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Stylesheet;
import org.jaxen.XPath;

/**
 * Implements the apply templates function in the stylesheet, similar to the XSLT equivalent.
 * a JSP include.
 */
public class ApplyTemplatesTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(ApplyTemplatesTag.class);

    /** Holds value of property mode. */
    private String mode;

    /** Holds the XPath object */
    private XPath select;

    public ApplyTemplatesTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    /** By default just evaluate the body */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final StylesheetTag tag = (StylesheetTag) findAncestorWithClass( StylesheetTag.class );
        if (tag == null) {
            throw new JellyTagException(
                "<applyTemplates> tag must be inside a <stylesheet> tag"
            );
        }
        final Stylesheet stylesheet = tag.getStylesheet();

        final XMLOutput oldOutput = tag.getStylesheetOutput();
        tag.setStylesheetOutput(output);

        final Object source = tag.getXPathSource();
        // for some reason, these DOM4J methods only throw Exception
        try {
            if ( select != null ) {
                stylesheet.applyTemplates( source, select, mode );
            }
            else {
                stylesheet.applyTemplates( source, mode );
            }
        }
        catch (final Exception e) {
            throw new JellyTagException(e);
        }

        tag.setStylesheetOutput(oldOutput);

        // #### should support MODE!!!

    }

    // Properties
    //-------------------------------------------------------------------------

    /** Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    public void setSelect( final XPath select ) {
        this.select = select;
    }
}
