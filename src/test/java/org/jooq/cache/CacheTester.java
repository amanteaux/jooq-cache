package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public abstract class CacheTester {
	
	@Test
	public void should_get_the_content_cached_if_it_has_been_put_to_the_cache() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key_bis", "content_bis");
		assertThat(cache.get("key")).isEqualTo("content");
		assertThat(cache.get("key_bis")).isEqualTo("content_bis");
	}
	
	@Test
	public void should_get_null_if_the_cache_is_empty() {
		Cache cache = cacheImplementation();
		
		assertThat(cache.get("key")).isNull();
	}
	
	@Test
	public void should_get_new_values_if_existing_cached_values_are_being_overriden() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key", "content_overrode");
		
		assertThat(cache.get("key")).isEqualTo("content_overrode");
	}
	
	@Test
	public void should_get_null_if_a_cache_with_existing_values_has_been_cleared() {
		Cache cache = cacheImplementation();
		
		cache.put("key", "content");
		cache.put("key_bis", "content_bis");
		
		cache.clear();
		
		assertThat(cache.get("key")).isNull();
		assertThat(cache.get("key_bis")).isNull();
	}
	
	abstract protected Cache cacheImplementation();

}
