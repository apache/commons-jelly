/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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

/** 
 * This class is a simple "bean-wrapper" for the {@link GridBagConstraints} class
 * which also tracks wether values are set allowing inheritance 
 *	(using {@link setBasedOn}.
 *
 * @author <a href="mailto:paul@activemath.org">Paul Libbrecht</a>
 * @version $Revision: $
 */
public class GridBagConstraintBean extends GridBagConstraints {

    private boolean gridxSet = false,
        gridySet = false,
        gridwidthSet = false,
        gridheightSet = false,
        weightxSet = false,
        weightySet = false,
        ipadxSet = false,
        ipadySet = false,
        anchorSet = false,
        fillSet = false;

    public GridBagConstraintBean() {
    }

    public int getGridx() {
        return gridx;
    }
    public void setGridx(int gridx) {
        this.gridx = gridx;
        this.gridxSet = true;
    }

    public int getGridy() {
        return gridy;
    }
    public void setGridy(int gridy) {
        this.gridy = gridy;
        this.gridySet = true;
    }

    public int getGridwidth() {
        return gridwidth;
    }
    public void setGridwidth(int gridwidth) {
        this.gridwidth = gridwidth;
        this.gridwidthSet = true;
    }

    public int getGridheight() {
        return gridheight;
    }
    public void setGridheight(int gridheight) {
        this.gridheight = gridheight;
        this.gridheightSet = true;
    }

    public double getWeightx() {
        return weightx;
    }
    public void setWeightx(double weightx) {
        this.weightx = weightx;
        this.weightxSet = true;
    }

    public double getWeighty() {
        return weighty;
    }
    public void setWeighty(double weighty) {
        this.weighty = weighty;
        this.weightySet = true;
    }

    public int getIpadx() {
        return ipadx;
    }
    public void setIpadx(int ipadx) {
        this.ipadx = ipadx;
        this.ipadxSet = true;
    }

    public int getIpady() {
        return ipady;
    }
    public void setIpady(int ipady) {
        this.ipady = ipady;
        this.ipadySet = true;
    }

    // TODO: provide better. insetstop, insetsbottom ??
    public Insets getInsets() {
        return insets;
    }
    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    /** Returns the lower-case variant of the constant-name
    	*	corresponding to the stored {@link #anchor} attribute.
    	*
    	*	@see	#anchor
    	*/
    public String getAnchor() {
        switch (this.anchor) {
            case CENTER :
                return "center";
            case NORTH :
                return "north";
            case NORTHEAST :
                return "northeast";
            case EAST :
                return "east";
            case SOUTHEAST :
                return "southeast";
            case SOUTH :
                return "south";
            case SOUTHWEST :
                return "southwest";
            case WEST :
                return "west";
            case NORTHWEST :
                return "northwest";
            default :
                throw new IllegalArgumentException("Anchor must be one of  the GridBagLayout constants: CENTER, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, or NORTHWEST.");
        }
    }

    /** Accepts one of the strings with the same name as the constants 
    	* and sets the {@link #anchor} value accordingly.
    	*	The accepted strings are case-insensitive.
    	*
    	*	@see #anchor
    	*/
    public void setAnchor(String anchorString) {
        String lcAnchorString = anchorString.toLowerCase();
        if (lcAnchorString.equals("center"))
            this.anchor = CENTER;
        else if (lcAnchorString.equals("north"))
            this.anchor = NORTH;
        else if (lcAnchorString.equals("northeast"))
            this.anchor = NORTHEAST;
        else if (lcAnchorString.equals("east"))
            this.anchor = EAST;
        else if (lcAnchorString.equals("southeast"))
            this.anchor = SOUTHEAST;
        else if (lcAnchorString.equals("south"))
            this.anchor = SOUTH;
        else if (lcAnchorString.equals("southwest"))
            this.anchor = SOUTHWEST;
        else if (lcAnchorString.equals("west"))
            this.anchor = WEST;
        else if (lcAnchorString.equals("northwest"))
            this.anchor = NORTHWEST;
        else
            throw new IllegalArgumentException("Anchor must be the name of one of  the GridBagLayoutConstants (case doesn't matter): center, north, northeast, east, southeast, south, southwest, west, or northwest.");
        this.anchorSet = true;
    }

    /** Returns the lower-case variant of the constant-name
    	*	corresponding to the stored {@link #fill} attribute.
    	*
    	*	@see	#fill
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
    /** Accepts one of the strings with the same name as the constants 
    	* and sets the {@link #fill} value accordingly.
    	*	The accepted strings are case-insensitive.
    	*
    	*	@see #fill
    	*/
    public void setFill(String fillString) {
        String lcFillString = fillString.toLowerCase();
        if (lcFillString.equals("none"))
            this.fill = NONE;
        else if (lcFillString.equals("horizontal"))
            this.fill = HORIZONTAL;
        else if (lcFillString.equals("vertical"))
            this.fill = VERTICAL;
        else if (lcFillString.equals("both"))
            this.fill = BOTH;
        else
            throw new IllegalArgumentException("Fill must be the name of one of  the GridBagLayoutConstants (case does not matter): NONE, HORIZONTAL, VERTICAL, BOTH.");
        this.fillSet = true;
    }

    /** Reads the values in the given grid-bag-constraint-bean that are set and sets
    	* them in this object if they have not been set yet.
    	*/
    public void setBasedOn(GridBagConstraintBean from) {
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
