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

import javax.sql.DataSource;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Tag handler for &lt;SetDataSource&gt; in JSTL, used to create
 * a simple DataSource for prototyping.</p>
 */
public class SetDataSourceTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SetDataSourceTag.class);

    protected Object dataSource;
    protected boolean dataSourceSpecified;
    protected String jdbcURL;
    protected String driverClassName;
    protected String userName;
    protected String password;

    private String scope = "page";
    private String var;

    //*********************************************************************
    // Constructor and initialization

    public SetDataSourceTag() {
    }

    //*********************************************************************
    // Accessor methods

    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        DataSource ds = null;

        if (dataSource != null) {
            ds = DataSourceUtil.getDataSource(dataSource, context);
        }
        else {
            if (dataSourceSpecified) {
                throw new JellyTagException(Resources.getMessage("SQL_DATASOURCE_NULL"));
            }

            final DataSourceWrapper dsw = new DataSourceWrapper();
            try {
                // set driver class iff provided by the tag
                if (driverClassName != null) {
                    dsw.setDriverClassName(driverClassName);
                }
            }
            catch (final Exception e) {
                log.error( "Could not load driver class: " + e, e );
                throw new JellyTagException(
                    Resources.getMessage("DRIVER_INVALID_CLASS", e.getMessage()));
            }
            dsw.setJdbcURL(jdbcURL);
            dsw.setUserName(userName);
            dsw.setPassword(password);
            ds = (DataSource) dsw;
        }

        if (var != null) {
            context.setVariable(var, ds);
        }
        else {
            context.setVariable("org.apache.commons.jelly.sql.DataSource", ds);
        }
    }

    public void setDataSource(final Object dataSource) {
        this.dataSource = dataSource;
        this.dataSourceSpecified = true;
    }

    public void setDriver(final String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Sets the scope of the variable to hold the
     * result.
     *
     */
    public void setScope(final String scope) {
        this.scope = scope;
    }

    public void setUrl(final String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public void setUser(final String userName) {
        this.userName = userName;
    }

    //*********************************************************************
    // Tag logic

    public void setVar(final String var) {
        this.var = var;
    }
}
