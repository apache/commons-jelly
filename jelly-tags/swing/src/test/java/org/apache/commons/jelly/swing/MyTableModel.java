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
package org.apache.commons.jelly.swing;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A sample table model
 */
public class MyTableModel extends AbstractTableModel {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(MyTableModel.class);

    public MyTableModel() {
    }

    final String[] columnNames = {
        "First Name",
        "Last Name",
        "Sport",
        "# of Years",
        "Vegetarian"
    };
    final Object[][] data = {
        {"Mary", "Campione",
         "Snowboarding", new Integer(5), new Boolean(false)},
        {"Alison", "Huml",
         "Rowing", new Integer(3), new Boolean(true)},
        {"Kathy", "Walrath",
         "Chasing toddlers", new Integer(2), new Boolean(false)},
        {"Mark", "Andrews",
         "Speed reading", new Integer(20), new Boolean(true)},
        {"Angela", "Lih",
         "Teaching high school", new Integer(4), new Boolean(false)}
        };

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(final int row, final int col) {
        return !(col < 2);
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    @Override
    public void setValueAt(final Object value, final int row, final int col) {
        if (log.isDebugEnabled()) {
            log.debug("Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }

        if (data[0][col] instanceof Integer
                && !(value instanceof Integer)) {
            //With JFC/Swing 1.1 and JDK 1.2, we need to create
            //an Integer from the value; otherwise, the column
            //switches to contain Strings.  Starting with v 1.3,
            //the table automatically converts value to an Integer,
            //so you only need the code in the 'else' part of this
            //'if' block.
            //XXX: See TableEditDemo.java for a better solution!!!
            try {
                data[row][col] = new Integer(value.toString());
                fireTableCellUpdated(row, col);
            } catch (final NumberFormatException e) {
                log.error( "The \"" + getColumnName(col)
                    + "\" column accepts only integer values.");
            }
        } else {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

}