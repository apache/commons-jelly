/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */

package org.apache.commons.jelly.tags.sql;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.jsp.jstl.sql.SQLExecutionTag;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;

/**
 * <p>Abstract base class for any SQL related tag in JSTL.  
 * 
 * @author Hans Bergsten
 * @author Justyna Horwat
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */

public abstract class SqlTagSupport extends TagSupport implements SQLExecutionTag {

    protected String var;
    protected String scope = "page";

    /*
     * The following properties take expression values, so the
     * setter methods are implemented by the expression type
     * specific subclasses.
     */
    protected Object rawDataSource;
    protected boolean dataSourceSpecified;
    protected String sql;

    /*
     * Instance variables that are not for attributes
     */
    private List parameters;
    protected boolean isPartOfTransaction;

    //*********************************************************************
    // Constructor and initialization

    public SqlTagSupport() {
    }

    //*********************************************************************
    // Accessor methods

    /**
     * Sets the name of the variable to hold the
     * result.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Sets the scope of the variable to hold the
     * result.
     */
    public void setScope(String scopeName) {
        this.scope = scopeName;
    }

    /**
     * Sets the SQL DataSource. DataSource can be
     * a String or a DataSource object.
     */
    public void setDataSource(Object dataSource) {
        this.rawDataSource = dataSource;
        this.dataSourceSpecified = true;
    }

    /**
     * Sets the SQL statement to use for the
     * query. The statement may contain parameter markers
     * (question marks, ?). If so, the parameter values must
     * be set using nested value elements.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }    
    

    //*********************************************************************
    // Public utility methods

    /**
     * Called by nested parameter elements to add PreparedStatement
     * parameter values.
     */
    public void addSQLParameter(Object o) {
        if (parameters == null) {
            parameters = new ArrayList();
        }
        parameters.add(o);
    }

    //*********************************************************************
    // Protected utility methods

    /**
     * @return true if there are SQL parameters
     */
    protected boolean hasParameters() {
        return parameters != null && parameters.size() > 0;
    }
    
    protected void clearParameters() {
        parameters = null;
    }

    protected Connection getConnection() throws JellyException, SQLException {
        // Fix: Add all other mechanisms
        Connection conn = null;
        isPartOfTransaction = false;

        TransactionTag parent =
            (TransactionTag) findAncestorWithClass(TransactionTag.class);
        if (parent != null) {
            if (dataSourceSpecified) {
                throw new JellyException(Resources.getMessage("ERROR_NESTED_DATASOURCE"));
            }
            conn = parent.getSharedConnection();
            isPartOfTransaction = true;
        }
        else {
            if ((rawDataSource == null) && dataSourceSpecified) {
                throw new JellyException(Resources.getMessage("SQL_DATASOURCE_NULL"));
            }
            DataSource dataSource = DataSourceUtil.getDataSource(rawDataSource, context);
            try {
                conn = dataSource.getConnection();
            }
            catch (Exception ex) {
                throw new JellyException(
                    Resources.getMessage("DATASOURCE_INVALID", ex.getMessage()));
            }
        }

        return conn;
    }

    protected void setParameters(PreparedStatement ps)
        throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                // The first parameter has index 1
                ps.setObject(i + 1, parameters.get(i));
            }
        }
    }
}
