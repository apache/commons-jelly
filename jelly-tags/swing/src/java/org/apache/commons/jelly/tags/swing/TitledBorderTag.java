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
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
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


    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( title == null) {
            throw new MissingAttributeException("title");
        }
        super.doTag(output);
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * Sets the color of the title for this border. Can be set via a nested <color> tag.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the Font to be used by the title. Can be set via a nested <font> tag.
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Sets the title text for this border.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the justification of the title. The String is case insensitive.
     * Possible values are {LEFT, CENTER, RIGHT, LEADING, TRAILING}
     */
    public void setTitleJustification(String titleJustification) {
        this.titleJustification = titleJustification;
    }

    /**
     * Sets the position of the title. The String is case insensitive.
     * Possible values are {ABOVE_TOP, TOP, BELOW_TOP, ABOVE_BOTTOM, BOTTOM, BELOW_BOTTOM}
     */
    public void setTitlePosition(String titlePosition) {
        this.titlePosition = titlePosition;
    }



    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Factory method to create a new Border instance.
     */
    protected Border createBorder() {
        if (border != null) {
            if (titleJustification != null && titlePosition != null) {
                int justification = asTitleJustification(titleJustification);
                int position = asTitlePosition(titlePosition);
                
                if (font != null) {
                    if (color != null) {
                        return BorderFactory.createTitledBorder(border, title, justification, position, font, color);
                    }
                    else {
                        return BorderFactory.createTitledBorder(border, title, justification, position, font);
                    }
                }
                return BorderFactory.createTitledBorder(border, title, justification, position);
            }
            return BorderFactory.createTitledBorder(border, title);
        }
        return BorderFactory.createTitledBorder(title);
    }

    /** 
     * @return the enumeration for the title justification
     */
    protected int asTitleJustification(String text) {    
        if (text.equalsIgnoreCase("LEFT")) {
            return TitledBorder.LEFT;
        }
        else if (text.equalsIgnoreCase("CENTER")) {
            return TitledBorder.CENTER;
        }
        else if (text.equalsIgnoreCase("RIGHT")) {
            return TitledBorder.RIGHT;
        }
        else if (text.equalsIgnoreCase("LEADING")) {
            return TitledBorder.LEADING;
        }
        else if (text.equalsIgnoreCase("TRAILING")) {
            return TitledBorder.TRAILING;
        }
        else {
            return TitledBorder.DEFAULT_JUSTIFICATION;
        }
    }

    /** 
     * @return the enumeration for the title position
     */
    protected int asTitlePosition(String text) {    
        if (text.equalsIgnoreCase("ABOVE_TOP")) {
            return TitledBorder.ABOVE_TOP;
        }
        else if (text.equalsIgnoreCase("TOP")) {
            return TitledBorder.TOP;
        }
        else if (text.equalsIgnoreCase("BELOW_TOP")) {
            return TitledBorder.BELOW_TOP;
        }
        else if (text.equalsIgnoreCase("ABOVE_BOTTOM")) {
            return TitledBorder.ABOVE_BOTTOM;
        }
        else if (text.equalsIgnoreCase("BOTTOM")) {
            return TitledBorder.BOTTOM;
        }
        else if (text.equalsIgnoreCase("BELOW_BOTTOM")) {
            return TitledBorder.BELOW_BOTTOM;
        }
        else {
            return TitledBorder.DEFAULT_POSITION;
        }
    }
}
