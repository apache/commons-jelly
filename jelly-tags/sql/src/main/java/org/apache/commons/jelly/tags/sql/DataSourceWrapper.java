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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.jelly.tags.Resources;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>A simple <code>DataSource</code> wrapper for the standard
 * <code>DriverManager</code> class.
 */
public class DataSourceWrapper implements DataSource {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DataSourceWrapper.class);

    private String driverClassName;
    private String jdbcURL;
    private String userName;
    private String password;

    /**
     * Returns a Connection using the DriverManager and all
     * set properties.
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        if (userName != null) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "Creating connection from url: " + jdbcURL + " userName: " + userName);
            }

            conn = DriverManager.getConnection(jdbcURL, userName, password);
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("Creating connection from url: " + jdbcURL);
            }

            conn = DriverManager.getConnection(jdbcURL);
        }
        if (log.isDebugEnabled()) {
            log.debug(
                "Created connection: " + conn );
        }
        return conn;
    }

    /**
     * Always throws a SQLException. User name and password are set
     * in the constructor and cannot be changed.
     */
    @Override
    public Connection getConnection(final String userName, final String password)
        throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

    /**
     * Always throws a SQLException. Not supported.
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

    /**
     * Always throws a SQLException. Not supported.
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(final Class<?> aClass) throws SQLException {
        return false;
    }

    public void setDriverClassName(final String driverClassName)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (log.isDebugEnabled()) {
            log.debug("Loading JDBC driver: [" + driverClassName + "]");
        }

        this.driverClassName = driverClassName;
        ClassLoaderUtils.getClassLoader(getClass()).loadClass(driverClassName).newInstance();
    }

    public void setJdbcURL(final String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    /**
     * Always throws a SQLException. Not supported.
     */
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

    /**
     * Always throws a SQLException. Not supported.
     */
    @Override
    public synchronized void setLogWriter(final PrintWriter out) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public <T> T unwrap(final Class<T> tClass) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }

}
