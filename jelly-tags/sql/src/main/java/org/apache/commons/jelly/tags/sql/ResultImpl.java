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

package org.apache.commons.jelly.tags.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.jsp.jstl.sql.Result;

/**
 * <p>This class creates a cached version of a <code>ResultSet</code>.
 * It's represented as a <code>Result</code> implementation, capable of
 * returning an array of <code>Row</code> objects containing a <code>Column</code>
 * instance for each column in the row.</p>
 *
 * <p>Note -- this is a private copy for the RI to avoid making the
 * corresponding class in javax.servlet.* public.</p>
 */

public class ResultImpl implements Result {
    private final List rowMap;
    private final List rowByIndex;
    private final String[] columnNames;
    private boolean isLimited;

    /**
     * This constructor is given a List of Maps where each Map represents a Row of data.
     * This constructor is typically used to create a Mock Object representing a result set.
     *
     * @param listOfMaps is a list of Maps where a Map represents a Row keyed by the column name
     */
    public ResultImpl(final List listOfMaps) {

        rowMap = new ArrayList();
        rowByIndex = new ArrayList();
        isLimited = false;

        // lets build up a Set of all the unique column names
        final HashSet keySet = new HashSet();
        for (final Iterator iter = listOfMaps.iterator(); iter.hasNext(); ) {
            final Map row = (Map) iter.next();
            keySet.addAll( row.keySet() );
        }

        // Create the column name array
        final int noOfColumns = keySet.size();
        columnNames = new String[noOfColumns];
        int i = 0;
        for (final Iterator iter = keySet.iterator(); iter.hasNext(); i++ ) {
            columnNames[i] = (String) iter.next();
        }

        // Now add each row to the result set
        for (final Iterator iter = listOfMaps.iterator(); iter.hasNext(); ) {
            final Map row = (Map) iter.next();

            final Object[] columns = new Object[noOfColumns];
            final SortedMap columnMap =
                new TreeMap(String.CASE_INSENSITIVE_ORDER);

            for (i = 0; i < noOfColumns; i++) {
                final String columnName = columnNames[i];
                final Object value = row.get(columnName);
                columns[i] = value;
                columnMap.put(columnName, value);
            }
            rowMap.add(columnMap);
            rowByIndex.add(columns);
        }
    }

    /**
     * This constructor reads the ResultSet and saves a cached
     * copy.
     *
     * @param rs an open <code>ResultSet</code>, positioned before the first
     * row
     * @param startRow beginning row to be cached
     * @param maxRows query maximum rows limit
     * @throws SQLException if a database error occurs
     */
    public ResultImpl(final ResultSet rs, final int startRow, final int maxRows)
        throws SQLException {

        rowMap = new ArrayList();
        rowByIndex = new ArrayList();

        final ResultSetMetaData rsmd = rs.getMetaData();
        final int noOfColumns = rsmd.getColumnCount();

        // Create the column name array
        columnNames = new String[noOfColumns];
        for (int i = 1; i <= noOfColumns; i++) {
            columnNames[i-1] = rsmd.getColumnName(i);
        }

        // Throw away all rows upto startRow
        for (int i = 0; i < startRow; i++) {
            rs.next();
        }

        // Process the remaining rows upto maxRows
        int processedRows = 0;
        while (rs.next()) {
            if (maxRows != -1 && processedRows == maxRows) {
                isLimited = true;
                break;
            }
            final Object[] columns = new Object[noOfColumns];
            final SortedMap columnMap =
                new TreeMap(String.CASE_INSENSITIVE_ORDER);

            // JDBC uses 1 as the lowest index!
            for (int i = 1; i <= noOfColumns; i++) {
                Object value =  rs.getObject(i);
                if (rs.wasNull()) {
                    value = null;
                }
                columns[i-1] = value;
                columnMap.put(columnNames[i-1], value);
            }
            rowMap.add(columnMap);
            rowByIndex.add(columns);
            processedRows++;
        }
    }

    /**
     * Returns an array of String objects. The array represents
     * the names of the columns arranged in the same order as in
     * the getRowsByIndex() method.
     *
     * @return an array of String[]
     */
    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Returns the number of rows in the cached ResultSet
     *
     * @return the number of cached rows, or -1 if the Result could
     *    not be initialized due to SQLExceptions
     */
    @Override
    public int getRowCount() {
        if (rowMap == null) {
            return -1;
        }
        return rowMap.size();
    }

    /**
     * Returns an array of SortedMap objects. The SortedMap
     * object key is the ColumnName and the value is the ColumnValue.
     * SortedMap was created using the CASE_INSENSITIVE_ORDER
     * Comparator so the key is the case insensitive representation
     * of the ColumnName.
     *
     * @return an array of Map, or null if there are no rows
     */
    @Override
    public SortedMap[] getRows() {
        if (rowMap == null) {
            return null;
        }

        //should just be able to return SortedMap[] object
        return (SortedMap []) rowMap.toArray(new SortedMap[0]);
    }

    /**
     * Returns an array of Object[] objects. The first index
     * designates the Row, the second the Column. The array
     * stores the value at the specified row and column.
     *
     * @return an array of Object[], or null if there are no rows
     */
    @Override
    public Object[][] getRowsByIndex() {
        if (rowByIndex == null) {
            return null;
        }

        //should just be able to return Object[][] object
        return (Object [][])rowByIndex.toArray(new Object[0][0]);
    }

    /**
     * Returns true of the query was limited by a maximum row setting
     *
     * @return true if the query was limited by a MaxRows attribute
     */
    @Override
    public boolean isLimitedByMaxRows() {
        return isLimited;
    }

}
