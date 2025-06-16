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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.swing.impl.Cell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a tabular row inside a &lt;tableLayout&gt; tag which mimicks the
 * &lt;tr&gt; HTML tag.
 */
public class TrTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TrTag.class);

    private TableLayoutTag tableLayoutTag;
    private final List cells = new ArrayList();
    private int rowIndex;

    public TrTag() {
    }

    /**
     * Adds a new cell to this row
     */
    public void addCell(final Component component, final GridBagConstraints constraints) throws JellyTagException {
        constraints.gridx = cells.size();
        cells.add(new Cell(constraints, component));
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        tableLayoutTag = (TableLayoutTag) findAncestorWithClass( TableLayoutTag.class );
        if (tableLayoutTag == null) {
            throw new JellyTagException( "this tag must be nested within a <tableLayout> tag" );
        }
        rowIndex = tableLayoutTag.nextRowIndex();
        cells.clear();

        invokeBody(output);

        // now iterate through the rows and add each one to the layout...
        int colIndex = 0;
        for (final Iterator iter = cells.iterator(); iter.hasNext(); ) {
            final Cell cell = (Cell) iter.next();
            final GridBagConstraints c = cell.getConstraints();

            // are we the last cell in the row
            if ( iter.hasNext() ) {
                // not last in row
                c.gridwidth = 1;
                c.gridx = colIndex++;
            }
            else {
                // end of row
                c.gridwidth = GridBagConstraints.REMAINDER;
            }
            c.gridy = rowIndex;

            // now lets add the cell to the table
            tableLayoutTag.addCell(cell);
        }
        cells.clear();
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the row index of this row
     */
    public int getRowIndex() {
        return rowIndex;
    }

}
