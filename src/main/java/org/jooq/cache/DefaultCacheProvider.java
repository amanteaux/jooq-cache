package org.jooq.cache;



public final class DefaultCacheProvider implements CacheProvider {
	
	static final DefaultCacheProvider INSTANCE = new DefaultCacheProvider();
	
	private DefaultCacheProvider() {
	}

	@Override
	public final Cache tableIndex() {
		return new DefaultCache();
	}

	@Override
	public final Cache fetchByQuery(String query) {
		// TODO do that a bit better :)
		return new DefaultCache();
	}

}
