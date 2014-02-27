package org.jooq.cache;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Handle cache instances
 * @author amanteaux
 *
 */
public final class CacheManager {
	
	
	private final LoadingCache<String, Cache> queriesCache;
	private final Cache tableIndex;
	
	public CacheManager(final CacheProvider cacheProvider) {
		this.queriesCache = CacheBuilder
				.newBuilder()
				.build(new CacheLoader<String, Cache>() {
					@Override
					public Cache load(String key) throws Exception {
						return cacheProvider.fetchByQuery(key);
					}
				});
		this.tableIndex = cacheProvider.tableIndex();
	}

	public final Cache fetchByQuery(String query) {
		try {
			return queriesCache.get(query);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final Cache tableIndex() {
		return tableIndex;
	}
	
	public final void clearByQuery(String query) {
		fetchByQuery(query).clear();
	}
	
	@SuppressWarnings("unchecked")
	public final void clearByTable(String tableName) {
		Set<String> queries = (Set<String>) tableIndex().get(tableName);
		if(queries != null) {
			for(String query : queries) {
				clearByQuery(query);
			}
		}
	}
	
}
