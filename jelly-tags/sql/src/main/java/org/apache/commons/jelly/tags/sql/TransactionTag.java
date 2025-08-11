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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;

/**
 * <p>Tag handler for &lt;Transaction&gt; in JSTL.
 */

public class TransactionTag extends TagSupport {

    //*********************************************************************
    // Private constants

    private static final String TRANSACTION_READ_COMMITTED = "read_committed";
    private static final String TRANSACTION_READ_UNCOMMITTED = "read_uncommitted";
    private static final String TRANSACTION_REPEATABLE_READ = "repeatable_read";
    private static final String TRANSACTION_SERIALIZABLE = "serializable";

    //*********************************************************************
    // Protected state

    protected Object rawDataSource;
    protected boolean dataSourceSpecified;

    //*********************************************************************
    // Private state

    private Connection conn;
    private int isolation = Connection.TRANSACTION_NONE;
    private int origIsolation;

    //*********************************************************************
    // Constructor and initialization

    public TransactionTag() {
    }

    /**
     * Restores the {@code Connection} to its initial state and
     * closes it.
     */
    protected void doFinally() {
        if (conn != null) {
            try {
                if (isolation != Connection.TRANSACTION_NONE
                    && isolation != origIsolation) {
                    conn.setTransactionIsolation(origIsolation);
                }
                conn.setAutoCommit(true);
                conn.close();
            }
            catch (final SQLException e) {
                // Not much we can do
            }
        }
        conn = null;
    }

    //*********************************************************************
    // Tag logic

    /**
     * Prepares for execution by setting the initial state, such as
     * getting the {@code Connection} and preparing it for
     * the transaction.
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        if (rawDataSource == null && dataSourceSpecified) {
            throw new JellyTagException(Resources.getMessage("SQL_DATASOURCE_NULL"));
        }

        final DataSource dataSource = DataSourceUtil.getDataSource(rawDataSource, context);

        try {
            conn = dataSource.getConnection();
            origIsolation = conn.getTransactionIsolation();
            if (origIsolation == Connection.TRANSACTION_NONE) {
                throw new JellyTagException(Resources.getMessage("TRANSACTION_NO_SUPPORT"));
            }
            if (isolation != Connection.TRANSACTION_NONE
                && isolation != origIsolation) {
                conn.setTransactionIsolation(isolation);
            }
            conn.setAutoCommit(false);
        }
        catch (final SQLException e) {
            throw new JellyTagException(
                Resources.getMessage("ERROR_GET_CONNECTION", e.getMessage()));
        }

        boolean finished = false;
        try {
            invokeBody(output);
            finished = true;
        }
        catch (final Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                }
                catch (final SQLException s) {
                    // Ignore to not hide orignal exception
                }
                doFinally();
            }
            throw new JellyTagException(e);
        }

        // lets commit
        try {
            conn.commit();
        }
        catch (final SQLException e) {
            throw new JellyTagException(
                Resources.getMessage("TRANSACTION_COMMIT_ERROR", e.getMessage()));
        }
        finally {
            doFinally();
        }
    }

    //*********************************************************************
    // Public utility methods

    /**
     * Called by nested parameter elements to get a reference to
     * the Connection.
     */
    public Connection getSharedConnection() {
        return conn;
    }

    /**
     * Sets the SQL DataSource. DataSource can be
     * a String or a DataSource object.
     */
    public void setDataSource(final Object dataSource) {
        this.rawDataSource = dataSource;
        this.dataSourceSpecified = true;
    }

    //*********************************************************************
    // Implementation methods methods

    /**
     * Sets the transaction isolation level.
     */
    public void setIsolation(final String iso) throws JellyTagException {

        if (iso == null) {
            throw new JellyTagException(Resources.getMessage("TRANSACTION_INVALID_ISOLATION"));
        }
        switch (iso) {
        case TRANSACTION_READ_COMMITTED:
            isolation = Connection.TRANSACTION_READ_COMMITTED;
            break;
        case TRANSACTION_READ_UNCOMMITTED:
            isolation = Connection.TRANSACTION_READ_UNCOMMITTED;
            break;
        case TRANSACTION_REPEATABLE_READ:
            isolation = Connection.TRANSACTION_REPEATABLE_READ;
            break;
        case TRANSACTION_SERIALIZABLE:
            isolation = Connection.TRANSACTION_SERIALIZABLE;
            break;
        default:
            throw new JellyTagException(Resources.getMessage("TRANSACTION_INVALID_ISOLATION"));
        }
    }

}
