package org.jooq;

import org.jooq.cache.CacheManager;

public interface CachedConfiguration extends ConfigurationExtended {
	
	/**
	 * @return The cache manager, should always return the same instance for all the DAO that interact between each other
	 */
	CacheManager cacheManager();

}
