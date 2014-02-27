package org.jooq.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.VisitListener;
import org.jooq.cache.CacheManager;

public class CacheQueryInformation {
 
	private final String query;
	private final String queryParameters;
	private final VisitListener visitListener;
	private final Set<String> referencedTables;
	private final CacheManager cacheManager; 

	public CacheQueryInformation(String query, List<Object> queryParameters, CacheManager cacheManager) {
		this.query = query;
		this.queryParameters = queryParameters.toString();
		this.referencedTables = new HashSet<String>();
		this.cacheManager = cacheManager;
		this.visitListener = new CachedVisitListener(referencedTables);
	}

	public String getQuery() {
		return query;
	}

	public String getQueryParameters() {
		return queryParameters;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	/**
	 * Available only when the query has been built
	 * 
	 * @return
	 */
	public Set<String> getReferencedTables() {
		return referencedTables;
	}

	public VisitListener getVisitListener() {
		return visitListener;
	}

}
