/*
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * 
 * @author Hans Bergsten
 * @author Justyna Horwat
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
     * named <code>javax.servlet.jsp.jstl.sql.dataSource</code>.
     */
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            conn = getConnection();
        }
        catch (SQLException e) {
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
                PreparedStatement ps = conn.prepareStatement(sqlStatement);
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
            Statement tempStatement = statement;
            statement = null;
            tempStatement.close();
        }
        catch (SQLException e) {
            throw new JellyTagException(sqlStatement + ": " + e.getMessage(), e);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    log.error("Caught exception while closing statement: " + e, e);
                }
            }
            if (conn != null && !isPartOfTransaction) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    log.error("Caught exception while closing connection: " + e, e);
                }
            }
            clearParameters();
        }
    }
}
