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
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.SQLExecutionTag;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Tag handler for &lt;Query&gt; in JSTL.  
 * 
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public class QueryTag extends TagSupport implements SQLExecutionTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(QueryTag.class);

    private String var;
    private String scope = "page";

    /*
     * The following properties take expression values, so the
     * setter methods are implemented by the expression type
     * specific subclasses.
     */
    protected Object rawDataSource;
    protected boolean dataSourceSpecified;
    protected String sql;
    protected int maxRows = -1;
    protected boolean maxRowsSpecified;
    protected int startRow;

    /*
     * Instance variables that are not for attributes
     */
    private Connection conn;
    private List parameters;
    private boolean isPartOfTransaction;

    //*********************************************************************
    // Constructor and initialization

    public QueryTag() {
    }

    //*********************************************************************
    // Accessor methods

    /**
     * Setter method for the name of the variable to hold the
     * result.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Setter method for the scope of the variable to hold the
     * result.
     */
    public void setScope(String scopeName) {
        this.scope = scopeName;
    }

    public void setDataSource(Object dataSource) {
        this.rawDataSource = dataSource;
        this.dataSourceSpecified = true;
    }

    /**
     * The index of the first row returned can be
     * specified using startRow.
     */
    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    /**
     * Query result can be limited by specifying
     * the maximum number of rows returned.
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
        this.maxRowsSpecified = true;
    }

    /**
     * Setter method for the SQL statement to use for the
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
    // Tag logic

    /**
     * <p>Execute the SQL statement, set either through the <code>sql</code>
     * attribute or as the body, and save the result as a variable
     * named by the <code>var</code> attribute in the scope specified
     * by the <code>scope</code> attribute, as an object that implements
     * the Result interface.
     *
     * <p>The connection used to execute the statement comes either
     * from the <code>DataSource</code> specified by the
     * <code>dataSource</code> attribute, provided by a parent action
     * element, or is retrieved from a JSP scope  attribute
     * named <code>javax.servlet.jstl.sql.dataSource</code>.
     */
    public void doTag(XMLOutput output) throws Exception {

        if (!maxRowsSpecified) {
            Object obj = context.getVariable("org.apache.commons.jelly.sql.maxRows");
            if (obj != null) {
                if (obj instanceof Integer) {
                    maxRows = ((Integer) obj).intValue();
                }
                else if (obj instanceof String) {
                    try {
                        maxRows = Integer.parseInt((String) obj);
                    }
                    catch (NumberFormatException nfe) {
                        throw new JellyException(
                            Resources.getMessage("SQL_MAXROWS_PARSE_ERROR", (String) obj),
                            nfe);
                    }
                }
                else {
                    throw new JellyException(Resources.getMessage("SQL_MAXROWS_INVALID"));
                }
            }
        }

        Result result = null;
        String sqlStatement = null;

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
                throw new JellyException(Resources.getMessage("SQL_NO_STATEMENT"));
            }
            /*
             * We shouldn't have a negative startRow or illegal maxrows
             */
            if ((startRow < 0) || (maxRows < -1)) {
                throw new JellyException(Resources.getMessage("PARAM_BAD_VALUE"));
            }

            /* 
             * Note! We must not use the setMaxRows() method on the
             * the statement to limit the number of rows, since the
             * Result factory must be able to figure out the correct
             * value for isLimitedByMaxRows(); there's no way to check
             * if it was from the ResultSet.
             */
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
            setParameters(ps, parameters);
            ResultSet rs = ps.executeQuery();
            result = new ResultImpl(rs, startRow, maxRows);
            context.setVariable(var, result);
        }
        catch (SQLException e) {
            throw new JellyException(sqlStatement + ": " + e.getMessage(), e);
        }
        finally {
            if (conn != null && !isPartOfTransaction) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                } // Not much we can do
                conn = null;
            }
            parameters = null;
        }
    }

    //*********************************************************************
    // Private utility methods

    private Connection getConnection() throws JellyException, SQLException {
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
                log.error( "Caught: " + ex, ex );
                throw new JellyException(
                    Resources.getMessage("DATASOURCE_INVALID", ex.getMessage()));
            }
        }

        return conn;
    }

    private void setParameters(PreparedStatement ps, List parameters)
        throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                // The first parameter has index 1
                ps.setObject(i + 1, parameters.get(i));
            }
        }
    }
}
