package org.jooq.cache.impl;

import org.jooq.cache.Cache;
import org.jooq.cache.CacheProvider;

public final class DefaultCacheProvider implements CacheProvider {
	
	@Override
	public final Cache tableIndex() {
		return new DefaultCache();
	}

	@Override
	public final Cache fetchByQuery(String query) {
		return new GuavaCache();
	}

}
