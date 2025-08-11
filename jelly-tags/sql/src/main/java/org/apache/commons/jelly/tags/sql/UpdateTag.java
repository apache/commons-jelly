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
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Tag handler for &lt;Update&gt; in JSTL.
 */

public class UpdateTag extends SqlTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(UpdateTag.class);

    /*
     * Instance variables that are not for attributes
     */
    private Connection conn;

    //*********************************************************************
    // Constructor and initialization

    public UpdateTag() {
    }

    //*********************************************************************
    // Tag logic

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
     * named {@code javax.servlet.jsp.jstl.sql.dataSource}.
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            conn = getConnection();
        }
        catch (final SQLException e) {
            throw new JellyTagException(sql + ": " + e.getMessage(), e);
        }

        /*
         * Use the SQL statement specified by the sql attribute, if any,
         * otherwise use the body as the statement.
         */
        String sqlStatement = null;
        if (sql != null) {
            sqlStatement = sql;
        }
        else {
            sqlStatement = getBodyText();
        }
        if (sqlStatement == null || sqlStatement.trim().length() == 0) {
            throw new JellyTagException(Resources.getMessage("SQL_NO_STATEMENT"));
        }

        Statement statement = null;
        int result = 0;
        try {
            if ( hasParameters() ) {
                final PreparedStatement ps = conn.prepareStatement(sqlStatement);
                statement = ps;
                setParameters(ps);
                result = ps.executeUpdate();
            }
            else {
                statement = conn.createStatement();
                result = statement.executeUpdate(sqlStatement);
            }
            if (var != null) {
                context.setVariable(var, new Integer(result));
            }

            // lets nullify before we close in case we get exceptions
            // while closing, we don't want to try to close again
            final Statement tempStatement = statement;
            statement = null;
            tempStatement.close();
        }
        catch (final SQLException e) {
            throw new JellyTagException(sqlStatement + ": " + e.getMessage(), e);
        }
        finally {
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
            }
            clearParameters();
        }
    }
}
