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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ExpressionTableModel extends AbstractTableModel {

    private JellyContext context;
    private List rows = new ArrayList();
    private MyTableColumnModel columnModel = new MyTableColumnModel();
        
    public ExpressionTableModel() {
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
    
    /**
     * Adds a new column definition to the table
     */
    public void addColumn(ExpressionTableColumn column) {
        columnModel.addColumn(column);
    }

    /**
     * Removes a column definition from the table
     */
    public void removeColumn(ExpressionTableColumn column) {
        columnModel.removeColumn(column);
    }
    
    
    // TableModel interface
    //-------------------------------------------------------------------------  
    public int getRowCount() {
        return rows.size();
    }
    
    public int getColumnCount() {
        return columnModel.getColumnCount();
    }
    
    public String getColumnName(int columnIndex) {
        String answer = null;
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return answer;
        }
        Object value = columnModel.getColumn(columnIndex).getHeaderValue();
        if (value != null) {
            return value.toString();
        }
        return answer;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object answer = null;
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return answer;
        }
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return answer;
        }
        Object row = rows.get(rowIndex);;
        ExpressionTableColumn column = (ExpressionTableColumn) columnModel.getColumn(columnIndex);
        if (row == null || column == null) {
            return answer;
        }
        return column.evaluateValue(this, row, rowIndex, columnIndex);
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

    /**
     * Sets the list of rows.
     * @param rows The rows to set
     */
    public void setRows(List rows) {
        this.rows = rows;
    }

    /**
     * Returns the context.
     * @return JellyContext
     */
    public JellyContext getContext() {
        return context;
    }

    /**
     * Sets the context.
     * @param context The context to set
     */
    public void setContext(JellyContext context) {
        this.context = context;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected static class MyTableColumnModel extends DefaultTableColumnModel {
        public List getColumnList() {
            return tableColumns;
        }
    };
        

}
