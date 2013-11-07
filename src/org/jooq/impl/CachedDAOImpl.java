package org.jooq.impl;

import org.jooq.ConfigurationExtended;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

public abstract class CachedDAOImpl<R extends UpdatableRecord<R>, P, T> extends ExtendedDAOImpl<R, P, T> {

	public CachedDAOImpl(final Table<R> table, final Class<P> type) {
		super(table, type);
	}

	protected CachedDAOImpl(final Table<R> table, final Class<P> type, final ConfigurationExtended configuration) {
		super(table, type, configuration);
	}
	
	protected<M extends Record> Result<M> fetchCached(ResultQuery<M> query) {
		query.attach(new CachedConfigurationExtended(configurationExtended()));
		return query.fetch();
	}

}