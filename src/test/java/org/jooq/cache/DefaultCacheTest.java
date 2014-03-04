package org.jooq.cache;

import org.jooq.cache.impl.DefaultCache;

public class DefaultCacheTest extends CacheTester {

	@Override
	protected Cache cacheImplementation() {
		return new DefaultCache();
	}

}
