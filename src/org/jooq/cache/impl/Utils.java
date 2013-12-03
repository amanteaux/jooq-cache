package org.jooq.cache.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jooq.Table;
import org.jooq.cache.Cache;

class Utils {
	
	static void putQuery(Cache<String, Map<String, CachedData>> queryCache, String query, String parameters, CachedData data) {
		if(queryCache.contains(query)) {
			Map<String, CachedData> cache = queryCache.get(query);
			cache.put(parameters, data);
		}
		else {
			Map<String, CachedData> cache = new ConcurrentHashMap<String, CachedData>();
			cache.put(parameters, data);
			queryCache.put(query, cache);
		}
	}
	
	static void putLinks(Cache<Table<?>, Set<String>> linksCache, String query, Set<Table<?>> tableReferences) {
		for(Table<?> tableReference : tableReferences) {
			if(linksCache.contains(tableReference)) {
				Set<String> links = linksCache.get(tableReference);
				if(!links.contains(query)) {
					links.add(query);
				}
			}
			else {
				// the synchronized is mandatory else a link can be overridden
				synchronized (linksCache) {
					Set<String> links = new CopyOnWriteArraySet<String>();
					links.add(query);
					linksCache.put(tableReference, links);
				}
			}
		}
	}
	
}
