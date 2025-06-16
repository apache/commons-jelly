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

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Layout tag which uses nested &lt;borderAlign&gt; tags to implement a BorderLayout
 */
public class BorderLayoutTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTagSupport.class);

    private int hgap;
    private int vgap;

    public BorderLayoutTag() {
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Creates a BorderLayout
     */
    @Override
    protected LayoutManager createLayoutManager() {
        return new BorderLayout(hgap, vgap);
    }

    /**
     * Returns the hgap.
     * @return int
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Returns the vgap.
     * @return int
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the horizontal gap in pixels.
     */
    public void setHgap(final int hgap) {
        this.hgap = hgap;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the vertical gap in pixels
     */
    public void setVgap(final int vgap) {
        this.vgap = vgap;
    }
}
