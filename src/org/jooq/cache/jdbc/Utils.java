package org.jooq.cache.jdbc;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jooq.Table;
import org.jooq.cache.CacheManager;
import org.jooq.cache.CacheProvider;

class Utils {
	
	static CachedData cachedData(CacheProvider cacheProvider, String query, String queryParameters) {
		return (CachedData) CacheManager.fetchByQuery(cacheProvider, query).get(queryParameters);
	}

	@SuppressWarnings("unchecked")
	static void cache(CacheProvider cacheProvider, Set<Table<?>> linkedTables, String query, String queryParameters, CachedData cachedData) {
		CacheManager.fetchByQuery(cacheProvider, query).put(queryParameters, cachedData);
		
		index((Map<String, Set<String>>) CacheManager.tableIndex(cacheProvider), linkedTables, query);
	}
	
	// TODO test this shit !
	private static void index(Map<String, Set<String>> tableIndexes, Set<Table<?>> linkedTables, String query) {
		for(Table<?> tableReference : linkedTables) {
			String tableReferenceName = tableReference.getName();
			
			if(!tableIndexes.containsKey(tableReferenceName)) {
				// the synchronized is mandatory else a link can be overridden
				synchronized (tableIndexes) {
					if(!tableIndexes.containsKey(tableReferenceName)) {
						Set<String> links = new CopyOnWriteArraySet<String>();
						links.add(query);
						tableIndexes.put(tableReferenceName, links);
						continue;
					}
				}
			}
			
			Set<String> links = tableIndexes.get(tableReference);
			if(!links.contains(query)) {
				links.add(query);
			}
		}
	}
	
}
