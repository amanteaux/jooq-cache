package org.jooq.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.VisitListener;

public class CacheQueryInformation {

	private final String query;
	private final List<Object> queryParameters;
	private final VisitListener visitListener;
	private final Set<String> referencedTables;

	public CacheQueryInformation(String query, List<Object> queryParameters) {
		this.query = query;
		this.queryParameters = queryParameters;
		this.referencedTables = new HashSet<String>();
		this.visitListener = new CachedVisitListener(referencedTables);
	}

	public String getQuery() {
		return query;
	}

	public List<Object> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * Available only when the query has been built
	 * 
	 * @return
	 */
	public Set<String> getLinkedTables() {
		return referencedTables;
	}

	public VisitListener getVisitListener() {
		return visitListener;
	}

}
