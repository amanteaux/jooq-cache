package org.jooq.cache;

import org.jooq.CachedConfiguration;
import org.jooq.impl.DefaultConfigurationExtended;

public class DefaultCachedConfiguration extends DefaultConfigurationExtended implements CachedConfiguration {

	private static final long serialVersionUID = -3764955303536523419L;
	
	private final CacheManager cacheManager;

	public DefaultCachedConfiguration(CacheProvider cacheProvider) {
		cacheManager = new CacheManager(cacheProvider);
	}
	
	public DefaultCachedConfiguration() {
		this(new DefaultCacheProvider());
	}

	@Override
	public CacheManager cacheManager() {
		return cacheManager;
	}

}
