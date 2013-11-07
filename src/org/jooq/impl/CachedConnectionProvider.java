package org.jooq.impl;

import java.sql.Connection;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

public class CachedConnectionProvider implements ConnectionProvider {
	
	private final ConnectionProvider delegate;

	public CachedConnectionProvider(ConnectionProvider connectionProvider) {
		this.delegate = connectionProvider;
	}

	@Override
	public Connection acquire() throws DataAccessException {
		return new CachedConnection(delegate.acquire());
	}

	@Override
	public void release(Connection connection) throws DataAccessException {
		delegate.release(connection);
	}

}
