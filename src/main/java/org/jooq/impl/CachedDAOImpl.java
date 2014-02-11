package org.jooq.impl;

import org.jooq.CachedConfiguration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.conf.ParamType;

// TODO should not extend ExtendedDAOImpl because update, delete and insert methods are final
public abstract class CachedDAOImpl<R extends UpdatableRecord<R>, P, T> extends ExtendedDAOImpl<R, P, T> {

	public CachedDAOImpl(final Table<R> table, final Class<P> type) {
		super(table, type);
	}

	protected CachedDAOImpl(final Table<R> table, final Class<P> type, final CachedConfiguration configuration) {
		super(table, type, configuration);
	}

	protected<M extends Record> Result<M> fetchCached(ResultQuery<M> query) {
		//@formatter:off
		query.attach(
			new CachedQueryConfiguration(
				configurationExtended(),
				new CacheQueryInformation(
					query.getSQL(ParamType.INDEXED),
					query.getBindValues(),
					cachedConfiguration().cacheProvider()
				)
			)
		);
		//@formatter:on
		return query.fetch();
	}
	

	// TODO implements update, insert and delete to clear the cache

	public CachedConfiguration cachedConfiguration() {
		return (CachedConfiguration) configuration();
	}

}