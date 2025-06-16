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
package org.apache.commons.jelly.tags.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a titled border.
 * The border will either be exported as a variable defined by the 'var' attribute
 * or will be set on the parent widget's border property
 */
public class TitledBorderTag extends BorderTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TitledBorderTag.class);

    private String title;
    private String titleJustification;
    private String titlePosition;
    private Border border;
    private Font font;
    private Color color;

    /**
     * @return the enumeration for the title justification
     */
    protected int asTitleJustification(final String text) {
        if (text.equalsIgnoreCase("LEFT")) {
            return TitledBorder.LEFT;
        }
        if (text.equalsIgnoreCase("CENTER")) {
            return TitledBorder.CENTER;
        }
        if (text.equalsIgnoreCase("RIGHT")) {
            return TitledBorder.RIGHT;
        }
        if (text.equalsIgnoreCase("LEADING")) {
            return TitledBorder.LEADING;
        }
        if (text.equalsIgnoreCase("TRAILING")) {
            return TitledBorder.TRAILING;
        }
        return TitledBorder.DEFAULT_JUSTIFICATION;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the enumeration for the title position
     */
    protected int asTitlePosition(final String text) {
        if (text.equalsIgnoreCase("ABOVE_TOP")) {
            return TitledBorder.ABOVE_TOP;
        }
        if (text.equalsIgnoreCase("TOP")) {
            return TitledBorder.TOP;
        }
        if (text.equalsIgnoreCase("BELOW_TOP")) {
            return TitledBorder.BELOW_TOP;
        }
        if (text.equalsIgnoreCase("ABOVE_BOTTOM")) {
            return TitledBorder.ABOVE_BOTTOM;
        }
        if (text.equalsIgnoreCase("BOTTOM")) {
            return TitledBorder.BOTTOM;
        }
        if (text.equalsIgnoreCase("BELOW_BOTTOM")) {
            return TitledBorder.BELOW_BOTTOM;
        }
        return TitledBorder.DEFAULT_POSITION;
    }

    /**
     * Factory method to create a new Border instance.
     */
    @Override
    protected Border createBorder() {
        if (border != null) {
            if (titleJustification != null && titlePosition != null) {
                final int justification = asTitleJustification(titleJustification);
                final int position = asTitlePosition(titlePosition);

                if (font != null) {
                    if (color != null) {
                        return BorderFactory.createTitledBorder(border, title, justification, position, font, color);
                    }
                    return BorderFactory.createTitledBorder(border, title, justification, position, font);
                }
                return BorderFactory.createTitledBorder(border, title, justification, position);
            }
            return BorderFactory.createTitledBorder(border, title);
        }
        return BorderFactory.createTitledBorder(title);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( title == null) {
            throw new MissingAttributeException("title");
        }
        super.doTag(output);
    }

    /**
     * Sets the color of the title for this border. Can be set via a nested {@code <color>} tag.
     */
    public void setColor(final Color color) {
        this.color = color;
    }

    /**
     * Sets the Font to be used by the title. Can be set via a nested {@code <font>} tag.
     */
    public void setFont(final Font font) {
        this.font = font;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the title text for this border.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Sets the justification of the title. The String is case insensitive.
     * Possible values are {LEFT, CENTER, RIGHT, LEADING, TRAILING}
     */
    public void setTitleJustification(final String titleJustification) {
        this.titleJustification = titleJustification;
    }

    /**
     * Sets the position of the title. The String is case insensitive.
     * Possible values are {ABOVE_TOP, TOP, BELOW_TOP, ABOVE_BOTTOM, BOTTOM, BELOW_BOTTOM}
     */
    public void setTitlePosition(final String titlePosition) {
        this.titlePosition = titlePosition;
    }
}
