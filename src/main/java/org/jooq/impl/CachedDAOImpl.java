package org.jooq.impl;

import java.util.Collection;
import java.util.List;

import org.jooq.CachedConfiguration;
import org.jooq.CachedDAO;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.cache.CacheManager;
import org.jooq.conf.ParamType;

public abstract class CachedDAOImpl<R extends UpdatableRecord<R>, P, T> implements CachedDAO<R, P, T> {
	
	private final ExtendedDAOImpl<R, P, T> delegate;
	private CachedConfiguration cachedConfiguration;

	public CachedDAOImpl(final Table<R> table, final Class<P> type) {
		this(table, type, null);
	}

	protected CachedDAOImpl(final Table<R> table, final Class<P> type, final CachedConfiguration cachedConfiguration) {
		this.delegate = new ExtendedDAOImpl<R, P, T>(table, type, cachedConfiguration) {

			@Override
			protected T getId(P object) {
				return CachedDAOImpl.this.getId(object);
			}

			@Override
			protected void setId(P object, Long id) {
				CachedDAOImpl.this.setId(object, id);
			}
			
		};
		this.cachedConfiguration = cachedConfiguration;
	}
	
	public CachedConfiguration configuration() {
		return cachedConfiguration;
	}
	
	public final void setConfiguration(CachedConfiguration configuration) {
		this.cachedConfiguration = configuration;
		delegate.setConfiguration(configuration);
	}
	
    // -------------------------------------------------------------------------
    // XXX: CachedDAO API
    // -------------------------------------------------------------------------

	protected<M extends Record> Result<M> fetchCached(ResultQuery<M> query) {
		//@formatter:off
		query.attach(
			new CachedQueryConfiguration(
				configuration(),
				new CacheQueryInformation(
					query.getSQL(ParamType.INDEXED),
					query.getBindValues(),
					configuration().cacheProvider()
				)
			)
		);
		//@formatter:on
		return query.fetch();
	}
	
	/**
	 * Clear all queries cached which have a reference to the table used in the DAO.<br/>
	 * For example, if the clearCache() method is called on a DAO for the table TableA :<br/>
	 * - The cache for the query : SELECT a,b,c FROM TableA WHERE a = ? will be cleared<br/>
	 * - The cache for the query : SELECT a,b,c,d FROM TableB t1 JOIN TableA t1 ON t1.e=t2.e WHERE a = ? will be cleared<br/>
	 * - The cache for the query : SELECT d FROM TableB WHERE a = ? will NOT be cleared
	 */
	public final void clearCache() {
		CacheManager.clearByTable(configuration().cacheProvider(), getTable().getName());
	}
	
    // ------------------------------------------------------------------------
    // XXX: ExtendedDAOImpl override
    // ------------------------------------------------------------------------

	public final void insert(Collection<P> objects) {
		delegate.insert(objects);
		clearCache();
	}
	
	public final void update(Collection<P> objects) {
		delegate.update(objects);
		clearCache();
	}
	
	public final void delete(Collection<P> objects) {
		delegate.delete(objects);
		clearCache();
	}

	public final void deleteById(Collection<T> ids) {
		delegate.deleteById(ids);
		clearCache();
	}
	
    // ------------------------------------------------------------------------
    // XXX: Delegate calls
    // ------------------------------------------------------------------------
	
	public RecordMapper<R, P> mapper() {
		return delegate.mapper();
	}

	public final void insert(P object) {
		delegate.insert(object);
	}

	public final void insert(P... objects) {
		delegate.insert(objects);
	}

	public final void update(P object) {
		delegate.update(object);
	}

	public final void update(P... objects) {
		delegate.update(objects);
	}

	public final void delete(P... objects) {
		delegate.delete(objects);
	}

	public final void deleteById(T... ids) {
		delegate.deleteById(ids);
	}

	public final boolean exists(P object) {
		return delegate.exists(object);
	}

	public final boolean existsById(T id) {
		return delegate.existsById(id);
	}

	public final long count() {
		return delegate.count();
	}

	public final List<P> findAll() {
		return delegate.findAll();
	}

	public final P findById(T id) {
		return delegate.findById(id);
	}

	public final <Z> List<P> fetch(Field<Z> field, Z... values) {
		return delegate.fetch(field, values);
	}

	public final <Z> P fetchOne(Field<Z> field, Z value) {
		return delegate.fetchOne(field, value);
	}

	public final Table<R> getTable() {
		return delegate.getTable();
	}

	public final Class<P> getType() {
		return delegate.getType();
	}

    // ------------------------------------------------------------------------
    // XXX: Template methods for generated subclasses
    // ------------------------------------------------------------------------
	
	protected abstract T getId(P object);
	
	/**
	 * Set a primary key value to the POJO
	 * @param object
	 * @param id
	 */
	protected abstract void setId(P object, Long id);

}