package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public abstract class CacheTester {
	
	@Test
	public void should_cache_content() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key_bis", "content_bis");
		assertThat(cache.get("key")).isEqualTo("content");
		assertThat(cache.get("key_bis")).isEqualTo("content_bis");
	}
	
	@Test
	public void should_return_null_if_empty() {
		Cache cache = cacheImplementation();
		
		assertThat(cache.get("key")).isNull();
	}
	
	@Test
	public void should_override_existing_cached_values() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key", "content_overrode");
		
		assertThat(cache.get("key")).isEqualTo("content_overrode");
	}
	
	@Test
	public void should_clear_all_values() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key_bis", "content_bis");
		
		cache.clear();
		
		assertThat(cache.get("key")).isNull();
		assertThat(cache.get("key_bis")).isNull();
	}
	
	abstract protected Cache cacheImplementation();

}
