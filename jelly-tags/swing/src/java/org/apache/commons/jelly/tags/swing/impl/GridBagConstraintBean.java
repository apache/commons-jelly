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
