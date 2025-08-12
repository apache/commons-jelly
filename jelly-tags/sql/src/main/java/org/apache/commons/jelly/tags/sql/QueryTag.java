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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Tag handler for &lt;Query&gt; in JSTL.
 */

public class QueryTag extends SqlTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(QueryTag.class);

    /*
     * The following properties take expression values, so the
     * setter methods are implemented by the expression type
     * specific subclasses.
     */
    protected int maxRows = -1;
    protected boolean maxRowsSpecified;
    protected int startRow;

    /*
     * Instance variables that are not for attributes
     */
    private Connection conn;

    //*********************************************************************
    // Constructor and initialization

    public QueryTag() {
    }

    //*********************************************************************
    // Accessor methods

    /**
     * <p>Execute the SQL statement, set either through the {@code sql}
     * attribute or as the body, and save the result as a variable
     * named by the {@code var} attribute in the scope specified
     * by the {@code scope} attribute, as an object that implements
     * the Result interface.
     *
     * <p>The connection used to execute the statement comes either
     * from the {@code DataSource} specified by the
     * {@code dataSource} attribute, provided by a parent action
     * element, or is retrieved from a JSP scope  attribute
     * named {@code javax.servlet.jstl.sql.dataSource}.
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        if (!maxRowsSpecified) {
            final Object obj = context.getVariable("org.apache.commons.jelly.sql.maxRows");
            if (obj != null) {
                if (obj instanceof Integer) {
                    maxRows = ((Integer) obj).intValue();
                }
                else if (obj instanceof String) {
                    try {
                        maxRows = Integer.parseInt((String) obj);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new JellyTagException(
                            Resources.getMessage("SQL_MAXROWS_PARSE_ERROR", (String) obj),
                            nfe);
                    }
                }
                else {
                    throw new JellyTagException(Resources.getMessage("SQL_MAXROWS_INVALID"));
                }
            }
        }

        Result result = null;
        String sqlStatement = null;

        log.debug( "About to lookup connection" );

        ResultSet rs = null;
        Statement statement = null;
        try {
            conn = getConnection();

            /*
             * Use the SQL statement specified by the sql attribute, if any,
             * otherwise use the body as the statement.
             */
            if (sql != null) {
                sqlStatement = sql;
            }
            else {
                sqlStatement = getBodyText();
            }
            if (sqlStatement == null || sqlStatement.trim().length() == 0) {
                throw new JellyTagException(Resources.getMessage("SQL_NO_STATEMENT"));
            }
            /*
             * We shouldn't have a negative startRow or illegal maxrows
             */
            if (startRow < 0 || maxRows < -1) {
                throw new JellyTagException(Resources.getMessage("PARAM_BAD_VALUE"));
            }

            /*
             * Note! We must not use the setMaxRows() method on the
             * the statement to limit the number of rows, since the
             * Result factory must be able to figure out the correct
             * value for isLimitedByMaxRows(); there's no way to check
             * if it was from the ResultSet.
             */
            if ( log.isDebugEnabled() ) {
                log.debug( "About to execute query: " + sqlStatement );
            }

            if ( hasParameters() ) {
                final PreparedStatement ps = conn.prepareStatement(sqlStatement);
                statement = ps;
                setParameters(ps);
                rs = ps.executeQuery();
            }
            else {
                statement = conn.createStatement();
                rs = statement.executeQuery(sqlStatement);
            }

            result = new ResultImpl(rs, startRow, maxRows);
            context.setVariable(var, result);

            // always close the result set first since it may be closed by
            // JDBC 3 when closing statements

            // lets nullify before we close in case we get exceptions
            // while closing, we don't want to try to close again
            final ResultSet tempRs = rs;
            rs = null;
            tempRs.close();
            final Statement tempStatement = statement;
            statement = null;
            tempStatement.close();
        }
        catch (final SQLException e) {
            throw new JellyTagException(sqlStatement + ": " + e.getMessage(), e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException e) {
                    log.error("Caught exception while closing result set: " + e, e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (final SQLException e) {
                    log.error("Caught exception while closing statement: " + e, e);
                }
            }
            if (conn != null && !isPartOfTransaction) {
                try {
                    conn.close();
                }
                catch (final SQLException e) {
                    log.error("Caught exception while closing connection: " + e, e);
                }
                conn = null;
            }
            clearParameters();
        }
    }

    /**
     * Query result can be limited by specifying
     * the maximum number of rows returned.
     */
    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
        this.maxRowsSpecified = true;
    }

    //*********************************************************************
    // Tag logic

    /**
     * The index of the first row returned can be
     * specified using startRow.
     */
    public void setStartRow(final int startRow) {
        this.startRow = startRow;
    }
}
