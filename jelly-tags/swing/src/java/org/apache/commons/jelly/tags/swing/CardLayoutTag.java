/*
 * Created on Nov 6, 2005
 *
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.CardLayout;
import java.awt.LayoutManager;

/** Implements CardLayout. Takes parameters hgap, vgap per the class. You can
 * set the "var" attribute of this tag, this will store the layout manager
 * in that context attribute, for later use.
 * 
 * @author Hans Gilde
 *
 */
public class CardLayoutTag extends LayoutTagSupport {
    private int hgap;
    private boolean hgapSet = false;
    private int vgap;
    private boolean vgapSet = false;

    protected LayoutManager createLayoutManager() {
        CardLayout cl = new CardLayout();

        if (hgapSet) {
            cl.setHgap(hgap);
        }
        
        if (vgapSet) {
            cl.setVgap(vgap);
        }
        
        return cl;
    }

    /**
     * @return Returns the hgap.
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * @param hgap The hgap to set.
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
        hgapSet = true;
    }

    /**
     * @return Returns the vgap.
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * @param vgap The vgap to set.
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
        vgapSet = true;
    }

}
