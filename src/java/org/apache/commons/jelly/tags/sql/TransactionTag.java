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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.Resources;

/**
 * <p>Tag handler for &lt;Transaction&gt; in JSTL.  
 * 
 * @author Hans Bergsten
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
     * Setter method for the SQL DataSource. DataSource can be
     * a String or a DataSource object.
     */
    public void setDataSource(Object dataSource) {
		this.rawDataSource = dataSource;
		this.dataSourceSpecified = true;
    }


	//*********************************************************************
	// Tag logic

	/**
	 * Prepares for execution by setting the initial state, such as
	 * getting the <code>Connection</code> and preparing it for
	 * the transaction.
	 */
	public void run(JellyContext context, XMLOutput output) throws Exception {

		if ((rawDataSource == null) && dataSourceSpecified) {
			throw new JellyException(Resources.getMessage("SQL_DATASOURCE_NULL"));
		}

		DataSource dataSource = DataSourceUtil.getDataSource(rawDataSource, context);

		try {
			conn = dataSource.getConnection();
			origIsolation = conn.getTransactionIsolation();
			if (origIsolation == Connection.TRANSACTION_NONE) {
				throw new JellyException(Resources.getMessage("TRANSACTION_NO_SUPPORT"));
			}
			if ((isolation != Connection.TRANSACTION_NONE)
				&& (isolation != origIsolation)) {
				conn.setTransactionIsolation(isolation);
			}
			conn.setAutoCommit(false);
		}
		catch (SQLException e) {
			throw new JellyException(
				Resources.getMessage("ERROR_GET_CONNECTION", e.getMessage()));
		}

		boolean finished = false;
		try {
			getBody().run( context, output );
			finished = true;
		}
		catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				}
				catch (SQLException s) {
					// Ignore to not hide orignal exception
				}
				doFinally();
			}
			throw e;
		}

		// lets commit			
		try {
			conn.commit();
		}
		catch (SQLException e) {
			throw new JellyException(
				Resources.getMessage("TRANSACTION_COMMIT_ERROR", e.getMessage()));
		}
		finally {
			doFinally();
		}
	}

	//*********************************************************************
	// Public utility methods

	/**
	 * Setter method for the transaction isolation level.
	 */
	public void setIsolation(String iso) throws JellyException {

		if (TRANSACTION_READ_COMMITTED.equals(iso)) {
			isolation = Connection.TRANSACTION_READ_COMMITTED;
		}
		else if (TRANSACTION_READ_UNCOMMITTED.equals(iso)) {
			isolation = Connection.TRANSACTION_READ_UNCOMMITTED;
		}
		else if (TRANSACTION_REPEATABLE_READ.equals(iso)) {
			isolation = Connection.TRANSACTION_REPEATABLE_READ;
		}
		else if (TRANSACTION_SERIALIZABLE.equals(iso)) {
			isolation = Connection.TRANSACTION_SERIALIZABLE;
		}
		else {
			throw new JellyException(Resources.getMessage("TRANSACTION_INVALID_ISOLATION"));
		}
	}

	/**
	 * Called by nested parameter elements to get a reference to
	 * the Connection.
	 */
	public Connection getSharedConnection() {
		return conn;
	}

	//*********************************************************************
	// Implementation methods methods

	/**
	 * Restores the <code>Connection</code> to its initial state and
	 * closes it.
	 */
	protected void doFinally() {
		if (conn != null) {
			try {
				if ((isolation != Connection.TRANSACTION_NONE)
					&& (isolation != origIsolation)) {
					conn.setTransactionIsolation(origIsolation);
				}
				conn.setAutoCommit(true);
				conn.close();
			}
			catch (SQLException e) {
				// Not much we can do
			}
		}
		conn = null;
	}

}