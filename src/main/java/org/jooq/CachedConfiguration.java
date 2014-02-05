package org.jooq;

import org.jooq.cache.CacheProvider;

public interface CachedConfiguration extends ConfigurationExtended {
	
	/**
	 * @return The cache provider, should always return the same instance
	 */
	CacheProvider cacheProvider();

}
