package org.jooq.impl;

import org.jooq.CachedConfiguration;
import org.jooq.DefaultConfigurationExtended;
import org.jooq.cache.CacheProvider;
import org.jooq.cache.DefaultCacheProvider;

public class DefaultCachedConfiguration extends DefaultConfigurationExtended implements CachedConfiguration {

	private static final long serialVersionUID = -3764955303536523419L;

	@Override
	public CacheProvider cacheProvider() {
		return DefaultCacheProvider.CACHE_PROVIDER;
	}

}
