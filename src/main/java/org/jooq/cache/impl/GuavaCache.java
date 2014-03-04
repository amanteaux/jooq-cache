package org.jooq.cache.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.jooq.cache.Cache;

import com.google.common.cache.CacheBuilder;

public class GuavaCache implements Cache {
	
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

}
