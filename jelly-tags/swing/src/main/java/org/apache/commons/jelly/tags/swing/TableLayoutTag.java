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

import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.swing.impl.Cell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Layout tag which mimicks the table, tr and td tags of HTML.
 */
public class TableLayoutTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTagSupport.class);

    private int rowCount;

    public TableLayoutTag() {
    }

    /**
     * Adds a new cell to the current grid
     */
    public void addCell(final Cell cell) throws JellyTagException {
        // find the parent container and add the component with the grid bag constraints
        addLayoutComponent(cell.getComponent(), cell.getConstraints());
    }

    /**
     * Creates a GridBagLayout
     */
    @Override
    protected LayoutManager createLayoutManager() {
        return new GridBagLayout();
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        rowCount = 0;
        super.doTag(output);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Creates a new row index for child {@code <tr>} tags
     */
    public int nextRowIndex() {
        return rowCount++;
    }
}
