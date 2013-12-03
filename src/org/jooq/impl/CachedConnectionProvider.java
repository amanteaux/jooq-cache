package org.jooq.impl;

import java.sql.Connection;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

public class CachedConnectionProvider implements ConnectionProvider {
	
	private final ConnectionProvider delegate;
	private final CacheQueryInformation queryInformation;

	public CachedConnectionProvider(ConnectionProvider connectionProvider, CacheQueryInformation queryInformation) {
		this.delegate = connectionProvider;
		this.queryInformation = queryInformation;
	}

	@Override
	public Connection acquire() throws DataAccessException {
		return new CachedConnection(delegate.acquire(), queryInformation);
	}

	@Override
	public void release(Connection connection) throws DataAccessException {
		delegate.release(connection);
	}

}
