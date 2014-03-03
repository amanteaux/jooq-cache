package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class DefaultCacheTest {

	@Test
	public void should_cache_content() {
		DefaultCache defaultCache = new DefaultCache();
		
		defaultCache.put("key", "content");
		defaultCache.put("key_bis", "content_bis");
		assertThat(defaultCache.get("key")).isEqualTo("content");
		assertThat(defaultCache.get("key_bis")).isEqualTo("content_bis");
	}
	
	@Test
	public void should_return_null_if_empty() {
		DefaultCache defaultCache = new DefaultCache();
		
		assertThat(defaultCache.get("key")).isNull();
	}
	
	@Test
	public void should_override_existing_cached_values() {
		DefaultCache defaultCache = new DefaultCache();
		
		defaultCache.put("key", "content");
		defaultCache.put("key", "content_overrode");
		
		assertThat(defaultCache.get("key")).isEqualTo("content_overrode");
	}
	
	@Test
	public void should_clear_all_values() {
		DefaultCache defaultCache = new DefaultCache();
		
		defaultCache.put("key", "content");
		defaultCache.put("key_bis", "content_bis");
		
		defaultCache.clear();
		
		assertThat(defaultCache.get("key")).isNull();
		assertThat(defaultCache.get("key_bis")).isNull();
	}
	
}
