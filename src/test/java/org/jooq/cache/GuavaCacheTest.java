package org.jooq.cache;

import org.jooq.cache.impl.GuavaCache;

public class GuavaCacheTest extends CacheTester {

	@Override
	protected Cache cacheImplementation() {
		return new GuavaCache();
	}

}
