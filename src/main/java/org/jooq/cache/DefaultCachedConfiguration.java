package org.jooq.cache;

import org.jooq.CachedConfiguration;
import org.jooq.DefaultConfigurationExtended;

public class DefaultCachedConfiguration extends DefaultConfigurationExtended implements CachedConfiguration {

	private static final long serialVersionUID = -3764955303536523419L;

	@Override
	public CacheProvider cacheProvider() {
		return DefaultCacheProvider.INSTANCE;
	}

}
