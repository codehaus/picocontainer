/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nanocontainer.persistence.ExceptionHandler;
import org.picocontainer.Startable;

/**
 * Connection component implementation which obtains a connection instance using a injected datasource. It has failover
 * support.
 * 
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class FailoverDataSourceConnection extends AbstractConnection implements Startable {

	private DataSource dataSource;

	private Connection connection;

	/**
	 * @param dataSource The DataSource instance where connections will be requested.
	 */
	public FailoverDataSourceConnection(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param dataSource The DataSource instance where connections will be requested.
	 * @param jdbcExceptionHandler The ExceptionHandler component instance.
	 */
	public FailoverDataSourceConnection(DataSource dataSource, ExceptionHandler exceptionHandler) {
		super(exceptionHandler);
		this.dataSource = dataSource;
	}

	/**
	 * @see org.nanocontainer.persistence.jdbc.AbstractConnection#getDelegatedConnection()
	 */
	protected Connection getDelegatedConnection() throws SQLException {
		if (connection == null) {
			connection = dataSource.getConnection();
		}

		return connection;
	}

	/**
	 * @see org.nanocontainer.persistence.jdbc.AbstractConnection#invalidateDelegatedConnection()
	 */
	protected void invalidateDelegatedConnection() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			// Do nothing
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// Do nothing
		}

		connection = null;
	}

	/**
	 * @see org.picocontainer.Startable#start()
	 */
	public void start() {
		// Do nothing
	}

	/**
	 * @see org.picocontainer.Startable#stop()
	 */
	public void stop() {
		try {
			connection.close();
		} catch (Exception e) {
			// Do nothing?
		}
	}

}
