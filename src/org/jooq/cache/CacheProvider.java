package org.jooq.cache;

import java.util.Map;
import java.util.Set;

import org.jooq.Table;
import org.jooq.cache.impl.CachedData;

public interface CacheProvider {
	
	Cache<String, Map<String, CachedData>> queryCache();
	
	Cache<Table<?>, Set<String>> linksCache();

}
