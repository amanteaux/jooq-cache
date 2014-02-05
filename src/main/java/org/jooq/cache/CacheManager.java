package org.jooq.cache;

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
	
	private static final String TABLE_INDEX_KEY = "_";

	private static final LoadingCache<CacheProvider, LoadingCache<String, Cache>> CACHE_INDEXES = CacheBuilder
	.newBuilder()
	.build(new CacheLoader<CacheProvider, LoadingCache<String, Cache>>() {
		@Override
		public LoadingCache<String, Cache> load(final CacheProvider cacheProvider) throws Exception {
			return CacheBuilder
			.newBuilder()
			.build(new CacheLoader<String, Cache>() {
				@Override
				public Cache load(String key) throws Exception {
					return TABLE_INDEX_KEY == key ? cacheProvider.tableIndex() : cacheProvider.fetchByQuery(key);
				}
			});
		}
	});
	
	public final static Cache fetchByQuery(CacheProvider cacheProvider, String query) {
		try {
			return CACHE_INDEXES.get(cacheProvider).get(query);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final static Cache tableIndex(CacheProvider cacheProvider) {
		try {
			return CACHE_INDEXES.get(cacheProvider).get(TABLE_INDEX_KEY);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
}
