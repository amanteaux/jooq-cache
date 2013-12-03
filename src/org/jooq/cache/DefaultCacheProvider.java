package org.jooq.cache;

import java.util.Map;
import java.util.Set;

import org.jooq.Table;
import org.jooq.cache.impl.CachedData;

public class DefaultCacheProvider implements CacheProvider {
	
	private final Cache<String, Map<String, CachedData>> queryCache;
	private final Cache<Table<?>, Set<String>> linksCache;
	
	public final static DefaultCacheProvider CACHE_PROVIDER = new DefaultCacheProvider();

	DefaultCacheProvider() {
		this.queryCache = new DefaultCache<String, Map<String, CachedData>>();
		this.linksCache = new DefaultCache<Table<?>, Set<String>>();
	}

	@Override
	public Cache<String, Map<String, CachedData>> queryCache() {
		return queryCache;
	}

	@Override
	public Cache<Table<?>, Set<String>> linksCache() {
		return linksCache;
	}

}
