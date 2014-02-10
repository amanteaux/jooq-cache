package org.jooq.cache.jdbc;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jooq.cache.Cache;
import org.jooq.cache.CacheManager;
import org.jooq.cache.CacheProvider;

class Utils {
	
	static CachedData cachedData(CacheProvider cacheProvider, String query, String queryParameters) {
		return (CachedData) CacheManager.fetchByQuery(cacheProvider, query).get(queryParameters);
	}

	static void cache(CacheProvider cacheProvider, Set<String> referencedTables, String query, String queryParameters, CachedData cachedData) {
		CacheManager.fetchByQuery(cacheProvider, query).put(queryParameters, cachedData);
		
		index(CacheManager.tableIndex(cacheProvider), referencedTables, query);
	}
	
	/**
	 * Add to the index cache the tables referenced in a query
	 * @param tableIndexes The table index which will be updated
	 * @param referencedTables The tables referenced in the query
	 * @param query
	 */
	static void index(Cache tableIndexes, Set<String> referencedTables, String query) {
		for(String tableReference : referencedTables) {
			index(tableIndexes, tableReference, query);
		}
	}
	
	/**
	 * Add to the index cache a table referenced in a query
	 * @param tableIndexes The table index which will be updated
	 * @param tableReference A table referenced in the query
	 * @param query
	 */
	@SuppressWarnings("unchecked")
	static void index(Cache tableIndexes, String tableReferenceName, String query) {
		if(tableIndexes.get(tableReferenceName) == null) {
			// the synchronized is mandatory else a link can be overridden
			synchronized (tableIndexes) {
				if(tableIndexes.get(tableReferenceName) == null) {
					CopyOnWriteArraySet<String> links = new CopyOnWriteArraySet<String>();
					links.add(query);
					tableIndexes.put(tableReferenceName, links);
					return;
				}
			}
		}
		
		Set<String> links = (Set<String>) tableIndexes.get(tableReferenceName);
		if(!links.contains(query)) {
			links.add(query);
		}
	}
	
}
