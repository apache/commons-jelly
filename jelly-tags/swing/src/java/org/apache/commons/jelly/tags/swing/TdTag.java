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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Represents a tabular cell inside a &lt;tl&gt; tag inside a &lt;tableLayout&gt; 
 * tag which mimicks the &lt;td&gt; HTML tag.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class TdTag extends TagSupport implements ContainerTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TdTag.class);

    private String align;
    private String valign;
    private int colspan = 1;
    private int rowspan = 1;
    private boolean colfill = false;
    private boolean rowfill = false;
    
    public TdTag() {
    }

    // ContainerTag interface
    //-------------------------------------------------------------------------                    
    
    /**
     * Adds a child component to this parent
     */
    public void addChild(Component component, Object constraints) throws JellyTagException {
        // add my child component to the layout manager
        TrTag tag = (TrTag) findAncestorWithClass( TrTag.class );
        if (tag == null) {
            throw new JellyTagException( "this tag must be nested within a <tr> tag" );
        }
        tag.addCell(component, createConstraints());
    }
    
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);
    }
    
    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /**
     * Sets the horizontal alignment to a case insensitive value of {LEFT, CENTER, RIGHT}
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * Sets the vertical alignment to a case insensitive value of {TOP, MIDDLE, BOTTOM}
     */
    public void setValign(String valign) {
        this.valign = valign;
    }

    
    /**
     * Sets the number of columns that this cell should span. The default value is 1
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    /**
     * Sets the number of rows that this cell should span. The default value is 1
     */
    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * Returns the colfill.
     * @return boolean
     */
    public boolean isColfill() {
        return colfill;
    }

    /**
     * Returns the rowfill.
     * @return boolean
     */
    public boolean isRowfill() {
        return rowfill;
    }

    /**
     * Sets whether or not this column should allow its component to stretch to fill the space available
     */
    public void setColfill(boolean colfill) {
        this.colfill = colfill;
    }

    /**
     * Sets whether or not this row should allow its component to stretch to fill the space available
     */
    public void setRowfill(boolean rowfill) {
        this.rowfill = rowfill;
    }


    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Factory method to create a new constraints object
     */
    protected GridBagConstraints createConstraints() {
        GridBagConstraints answer = new GridBagConstraints();
        answer.anchor = getAnchor();
        if (colspan < 1) {
            colspan = 1;
        }
        if (rowspan < 1) {
            rowspan = 1;
        }
        if (isColfill())  {
            answer.fill = isRowfill()
                ? GridBagConstraints.BOTH 
                : GridBagConstraints.HORIZONTAL;
        }
        else {
            answer.fill = isRowfill()
                ? GridBagConstraints.VERTICAL 
                : GridBagConstraints.NONE;
        }
        answer.weightx = 0.2;
        answer.weighty = 0;
        answer.gridwidth = colspan;
        answer.gridheight = rowspan;
        return answer;
    }
    
    /**
     * @return the GridBagConstraints enumeration for achor
     */
    protected int getAnchor() {
        boolean isTop = "top".equalsIgnoreCase(valign);
        boolean isBottom = "bottom".equalsIgnoreCase(valign);
        
        if ("center".equalsIgnoreCase(align)) {
            if (isTop) {
                return GridBagConstraints.NORTH;
            }
            else if (isBottom) {
                return GridBagConstraints.SOUTH;
            }
            else {
                return GridBagConstraints.CENTER;
            }
        }
        else if ("right".equalsIgnoreCase(align)) {
            if (isTop) {
                return GridBagConstraints.NORTHEAST;
            }
            else if (isBottom) {
                return GridBagConstraints.SOUTHEAST;
            }
            else {
                return GridBagConstraints.EAST;
            }
        }
        else {
            // defaults to left
            if (isTop) {
                return GridBagConstraints.NORTHWEST;
            }
            else if (isBottom) {
                return GridBagConstraints.SOUTHWEST;
            }
            else {
                return GridBagConstraints.WEST;
            }
        }
    }
}
