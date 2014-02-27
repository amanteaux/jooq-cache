package org.jooq.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;



public final class DefaultCacheProvider implements CacheProvider {
	
	@Override
	public final Cache tableIndex() {
		return new DefaultCache();
	}

	@Override
	public final Cache fetchByQuery(String query) {
		return new Cache() {
			
			private final com.google.common.cache.Cache<String, Serializable> cache = CacheBuilder
					.newBuilder()
					.maximumSize(10000)
					.expireAfterAccess(2, TimeUnit.HOURS)
					.build();
			
			@Override
			public Serializable put(String key, Serializable value) {
				cache.put(key, value);
				return value;
			}
			
			@Override
			public Serializable get(String key) {
				return cache.getIfPresent(key);
			}
			
			@Override
			public void clear() {
				cache.invalidateAll();
			}
		};
	}

}
