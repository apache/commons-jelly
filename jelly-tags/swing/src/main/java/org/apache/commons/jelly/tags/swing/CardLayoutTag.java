/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.swing;

import java.awt.CardLayout;
import java.awt.LayoutManager;

/** Implements CardLayout. Takes parameters hgap, vgap per the class. You can
 * set the "var" attribute of this tag, this will store the layout manager
 * in that context attribute, for later use.
 *
 */
public class CardLayoutTag extends LayoutTagSupport {
    private int hgap;
    private boolean hgapSet = false;
    private int vgap;
    private boolean vgapSet = false;

    @Override
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
     * @return the hgap.
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * @return the vgap.
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * @param hgap The hgap to set.
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
        hgapSet = true;
    }

    /**
     * @param vgap The vgap to set.
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
        vgapSet = true;
    }

}
