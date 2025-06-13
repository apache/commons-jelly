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
    @Override
    public void addChild(final Component component, final Object constraints) throws JellyTagException {
        // add my child component to the layout manager
        final TrTag tag = (TrTag) findAncestorWithClass( TrTag.class );
        if (tag == null) {
            throw new JellyTagException( "this tag must be nested within a <tr> tag" );
        }
        tag.addCell(component, createConstraints());
    }

    /**
     * Factory method to create a new constraints object
     */
    protected GridBagConstraints createConstraints() {
        final GridBagConstraints answer = new GridBagConstraints();
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

    // Properties
    //-------------------------------------------------------------------------

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);
    }

    /**
     * @return the GridBagConstraints enumeration for achor
     */
    protected int getAnchor() {
        final boolean isTop = "top".equalsIgnoreCase(valign);
        final boolean isBottom = "bottom".equalsIgnoreCase(valign);

        if ("center".equalsIgnoreCase(align)) {
            if (isTop) {
                return GridBagConstraints.NORTH;
            }
            if (isBottom) {
                return GridBagConstraints.SOUTH;
            }
            return GridBagConstraints.CENTER;
        }
        if ("right".equalsIgnoreCase(align)) {
            if (isTop) {
                return GridBagConstraints.NORTHEAST;
            }
            if (isBottom) {
                return GridBagConstraints.SOUTHEAST;
            }
            return GridBagConstraints.EAST;
        }
        if (isTop) {
            return GridBagConstraints.NORTHWEST;
        }
        if (isBottom) {
            return GridBagConstraints.SOUTHWEST;
        }
        return GridBagConstraints.WEST;
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
     * Sets the horizontal alignment to a case insensitive value of {LEFT, CENTER, RIGHT}
     */
    public void setAlign(final String align) {
        this.align = align;
    }

    /**
     * Sets whether or not this column should allow its component to stretch to fill the space available
     */
    public void setColfill(final boolean colfill) {
        this.colfill = colfill;
    }

    /**
     * Sets the number of columns that this cell should span. The default value is 1
     */
    public void setColspan(final int colspan) {
        this.colspan = colspan;
    }

    /**
     * Sets whether or not this row should allow its component to stretch to fill the space available
     */
    public void setRowfill(final boolean rowfill) {
        this.rowfill = rowfill;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the number of rows that this cell should span. The default value is 1
     */
    public void setRowspan(final int rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * Sets the vertical alignment to a case insensitive value of {TOP, MIDDLE, BOTTOM}
     */
    public void setValign(final String valign) {
        this.valign = valign;
    }
}
