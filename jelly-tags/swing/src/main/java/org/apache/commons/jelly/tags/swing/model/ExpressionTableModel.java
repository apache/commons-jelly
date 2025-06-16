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
package org.apache.commons.jelly.tags.swing.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.jelly.JellyContext;

/**
 * A Swing TableModel that uses a List of rows with pluggable Expressions
 * to evaluate the value of the cells
 */
public class ExpressionTableModel extends AbstractTableModel {

    // Implementation methods
    //-------------------------------------------------------------------------
    protected static class MyTableColumnModel extends DefaultTableColumnModel {
        public List getColumnList() {
            return tableColumns;
        }
    }
    private JellyContext context;
    private List rows = new ArrayList();

    private final MyTableColumnModel columnModel = new MyTableColumnModel();

    public ExpressionTableModel() {
    }

    /**
     * Adds a new column definition to the table
     */
    public void addColumn(final ExpressionTableColumn column) {
        columnModel.addColumn(column);
    }

    @Override
    public int getColumnCount() {
        return columnModel.getColumnCount();
    }

    /**
     * Returns the column definitions.
     * @return List
     */
    public List getColumnList() {
        return columnModel.getColumnList();
    }

    /**
     * @return the TableColumnModel
     */
    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        final String answer = null;
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return answer;
        }
        final Object value = columnModel.getColumn(columnIndex).getHeaderValue();
        if (value != null) {
            return value.toString();
        }
        return answer;
    }

    /**
     * Returns the context.
     * @return JellyContext
     */
    public JellyContext getContext() {
        return context;
    }

    // TableModel interface
    //-------------------------------------------------------------------------
    @Override
    public int getRowCount() {
        return rows.size();
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the list of rows.
     * @return List
     */
    public List getRows() {
        return rows;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Object answer = null;
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return answer;
        }
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return answer;
        }
        final Object row = rows.get(rowIndex);
        final ExpressionTableColumn column = (ExpressionTableColumn) columnModel.getColumn(columnIndex);
        if (row == null || column == null) {
            return answer;
        }
        return column.evaluateValue(this, row, rowIndex, columnIndex);
    }

    /**
     * Removes a column definition from the table
     */
    public void removeColumn(final ExpressionTableColumn column) {
        columnModel.removeColumn(column);
    }

    /**
     * Sets the context.
     * @param context The context to set
     */
    public void setContext(final JellyContext context) {
        this.context = context;
    }

    /**
     * Sets the list of rows.
     * @param rows The rows to set
     */
    public void setRows(final List rows) {
        this.rows = rows;
    }

}
