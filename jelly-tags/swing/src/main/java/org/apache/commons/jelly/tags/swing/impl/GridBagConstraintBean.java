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
package org.apache.commons.jelly.tags.swing.impl;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a simple "bean-wrapper" for the {@link GridBagConstraints} class
 * which also tracks wether values are set allowing inheritance
 *    (using {@link setBasedOn}.
 */
public class GridBagConstraintBean extends GridBagConstraints {

    /** Logging output */
    private static final Log LOG = LogFactory.getLog(GridBagConstraintBean.class);
    /** Error message */
    private static final String ILLEGAL_ANCHOR_MSG = "Anchor must be one of  the GridBagLayout constants for the current Java version.";
    private boolean gridxSet = false;
    private boolean gridySet = false;
    private boolean gridwidthSet = false;
    private boolean gridheightSet = false;
    private boolean weightxSet = false;
    private boolean weightySet = false;
    private boolean ipadxSet = false;
    private boolean ipadySet = false;

    private boolean anchorSet = false;

    private boolean fillSet = false;

    public GridBagConstraintBean() {
    }

    /** Returns the lower-case variant of the constant-name
        *    corresponding to the stored {@link #anchor} attribute.
        *
        *    @see    #anchor
        */
    public String getAnchor() {
        switch (this.anchor) {
        case CENTER:
            return "center";
        case NORTH:
            return "north";
        case NORTHEAST:
            return "northeast";
        case EAST:
            return "east";
        case SOUTHEAST:
            return "southeast";
        case SOUTH:
            return "south";
        case SOUTHWEST:
            return "southwest";
        case WEST:
            return "west";
        case NORTHWEST:
            return "northwest";
        }
        if (this.anchor == getByReflection("LINE_START")) {
            return "line_start";
        }
        if (this.anchor == getByReflection("LINE_END")) {
            return "line_end";
        }
        if (this.anchor == getByReflection("PAGE_START")) {
            return "page_start";
        }
        if (this.anchor == getByReflection("PAGE_END")) {
            return "page_end";
        }
        if (this.anchor == getByReflection("FIRST_LINE_START")) {
            return "first_line_start";
        }
        if (this.anchor == getByReflection("FIRST_LINE_END")) {
            return "first_line_end";
        }
        if (this.anchor == getByReflection("LAST_LINE_START")) {
            return "last_line_start";
        }
        if (this.anchor == getByReflection("LAST_LINE_END")) {
            return "last_line_end";
        }
        throw new IllegalArgumentException(ILLEGAL_ANCHOR_MSG);
    }

    private int getByReflection(final String field) {
        try {
            final Field f = getClass().getField(field);
            final Integer rv = (Integer) f.get(this);
            return rv.intValue();
        } catch (final SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            LOG.debug(e);
            throw new IllegalArgumentException(ILLEGAL_ANCHOR_MSG);
        }
    }

    /** Returns the lower-case variant of the constant-name
        *    corresponding to the stored {@link #fill} attribute.
        *
        *    @see    #fill
        */
    public String getFill() {
        switch (fill) {
            case NONE :
                return "none";
            case HORIZONTAL :
                return "horizontal";
            case VERTICAL :
                return "vertical";
            case BOTH :
                return "both";
            default :
                throw new IllegalArgumentException("Fill must be the name of one of  the GridBagLayoutConstants: NONE, HORIZONTAL, VERTICAL, BOTH.");
        }
    }
    public int getGridheight() {
        return gridheight;
    }

    public int getGridwidth() {
        return gridwidth;
    }
    public int getGridx() {
        return gridx;
    }

    public int getGridy() {
        return gridy;
    }
    // TODO: provide better. insetstop, insetsbottom ??
    public Insets getInsets() {
        return insets;
    }

    public int getIpadx() {
        return ipadx;
    }
    public int getIpady() {
        return ipady;
    }

    public double getWeightx() {
        return weightx;
    }
    public double getWeighty() {
        return weighty;
    }

    /** Accepts one of the strings with the same name as the constants
        * and sets the {@link #anchor} value accordingly.
        *    The accepted strings are case-insensitive.
        *
        *    @see #anchor
        */
    public void setAnchor(final String anchorString) {
        final String lcAnchorString = anchorString.toLowerCase();
        if (lcAnchorString == null) {
            throw new IllegalArgumentException("Anchor must be the name of one of  the GridBagLayoutConstants (case doesn't matter): center, north, northeast, east, southeast, south, southwest, west, or northwest.");
        }
        switch (lcAnchorString) {
        case "center":
            this.anchor = CENTER;
            break;
        case "north":
            this.anchor = NORTH;
            break;
        case "northeast":
            this.anchor = NORTHEAST;
            break;
        case "east":
            this.anchor = EAST;
            break;
        case "southeast":
            this.anchor = SOUTHEAST;
            break;
        case "south":
            this.anchor = SOUTH;
            break;
        case "southwest":
            this.anchor = SOUTHWEST;
            break;
        case "west":
            this.anchor = WEST;
            break;
        case "northwest":
            this.anchor = NORTHWEST;
            break;
        case "page_start":
            this.anchor = getByReflection("PAGE_START");
            break;
        case "page_end":
            this.anchor = getByReflection("PAGE_END");
            break;
        case "line_start":
            this.anchor = getByReflection("LINE_START");
            break;
        case "line_end":
            this.anchor = getByReflection("LINE_END");
            break;
        case "first_line_start":
            this.anchor = getByReflection("FIRST_LINE_START");
            break;
        case "first_line_end":
            this.anchor = getByReflection("FIRST_LINE_END");
            break;
        case "last_line_end":
            this.anchor = getByReflection("LAST_LINE_END");
            break;
        case "last_line_start":
            this.anchor = getByReflection("LAST_LINE_START");
            break;
        default:
            throw new IllegalArgumentException("Anchor must be the name of one of  the GridBagLayoutConstants (case doesn't matter): center, north, northeast, east, southeast, south, southwest, west, or northwest.");
        }
        this.anchorSet = true;
    }
    /** Reads the values in the given grid-bag-constraint-bean that are set and sets
        * them in this object if they have not been set yet.
        */
    public void setBasedOn(final GridBagConstraintBean from) {
        if (!gridxSet && from.gridxSet) {
            gridx = from.gridx;
            this.gridxSet = true;
        }
        if (!gridySet && from.gridySet) {
            gridy = from.gridy;
            this.gridySet = true;
        }
        if (!gridwidthSet && from.gridwidthSet) {
            gridwidth = from.gridwidth;
            this.gridwidthSet = true;
        }
        if (!gridheightSet && from.gridheightSet) {
            gridheight = from.gridheight;
            this.gridheightSet = true;
        }
        if (!weightxSet && from.weightxSet) {
            weightx = from.weightx;
            this.weightxSet = true;
        }
        if (!weightySet && from.weightySet) {
            weighty = from.weighty;
            this.weightySet = true;
        }
        if (!ipadxSet && from.ipadxSet) {
            ipadx = from.ipadx;
            this.ipadxSet = true;
        }
        if (!ipadySet && from.ipadySet) {
            ipady = from.ipady;
            this.ipadySet = true;
        }
        if (!fillSet && from.fillSet) {
            fill = from.fill;
            this.fillSet = true;
        }
        if (!anchorSet && from.anchorSet) {
            anchor = from.anchor;
            this.anchorSet = true;
        }
    }

    /** Accepts one of the strings with the same name as the constants
        * and sets the {@link #fill} value accordingly.
        *    The accepted strings are case-insensitive.
        *
        *    @see #fill
        */
    public void setFill(final String fillString) {
        final String lcFillString = fillString.toLowerCase();
        if (lcFillString == null) {
            throw new IllegalArgumentException("Fill must be the name of one of  the GridBagLayoutConstants (case does not matter): NONE, HORIZONTAL, VERTICAL, BOTH.");
        }
        switch (lcFillString) {
        case "none":
            this.fill = NONE;
            break;
        case "horizontal":
            this.fill = HORIZONTAL;
            break;
        case "vertical":
            this.fill = VERTICAL;
            break;
        case "both":
            this.fill = BOTH;
            break;
        default:
            throw new IllegalArgumentException("Fill must be the name of one of  the GridBagLayoutConstants (case does not matter): NONE, HORIZONTAL, VERTICAL, BOTH.");
        }
        this.fillSet = true;
    }
    public void setGridheight(final int gridheight) {
        this.gridheight = gridheight;
        this.gridheightSet = true;
    }

    public void setGridwidth(final int gridwidth) {
        this.gridwidth = gridwidth;
        this.gridwidthSet = true;
    }
    public void setGridx(final int gridx) {
        this.gridx = gridx;
        this.gridxSet = true;
    }

    public void setGridy(final int gridy) {
        this.gridy = gridy;
        this.gridySet = true;
    }

    public void setInsets(final Insets insets) {
        this.insets = insets;
    }

    public void setIpadx(final int ipadx) {
        this.ipadx = ipadx;
        this.ipadxSet = true;
    }
    public void setIpady(final int ipady) {
        this.ipady = ipady;
        this.ipadySet = true;
    }

    public void setWeightx(final double weightx) {
        this.weightx = weightx;
        this.weightxSet = true;
    }

    public void setWeighty(final double weighty) {
        this.weighty = weighty;
        this.weightySet = true;
    }

    @Override
    public String toString() {
        return "GridBagConstraintBean["
            + "gridx="
            + gridx
            + ", gridy="
            + gridy
            + ", gridwidth="
            + gridwidth
            + ", gridheight="
            + gridheight
            + ", weightx="
            + weightx
            + ", weighty="
            + weighty
            + ", ipadx="
            + ipadx
            + ", ipady="
            + ipady
            + ", anchor="
            + getAnchor()
            + ", fill="
            + getFill()
            + ", insets="
            + insets
            + "]";
    }

} // class GridBagConstraintsBean
