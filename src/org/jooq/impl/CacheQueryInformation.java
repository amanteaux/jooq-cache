package org.jooq.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.Table;
import org.jooq.VisitListener;
import org.jooq.cache.CacheProvider;

public class CacheQueryInformation {
 
	private final String query;
	private final String queryParameters;
	private final VisitListener visitListener;
	private final Set<Table<?>> referencedTables;
	private final CacheProvider cacheProvider; 

	public CacheQueryInformation(String query, List<Object> queryParameters, CacheProvider cacheProvider) {
		this.query = query;
		this.queryParameters = queryParameters.toString();
		this.referencedTables = new HashSet<Table<?>>();
		this.cacheProvider = cacheProvider;
		this.visitListener = new CachedVisitListener(referencedTables);
	}

	public String getQuery() {
		return query;
	}

	public String getQueryParameters() {
		return queryParameters;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}

	/**
	 * Available only when the query has been built
	 * 
	 * @return
	 */
	public Set<Table<?>> getLinkedTables() {
		return referencedTables;
	}

	public VisitListener getVisitListener() {
		return visitListener;
	}

}
