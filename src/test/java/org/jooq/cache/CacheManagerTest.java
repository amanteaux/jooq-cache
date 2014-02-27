package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class CacheManagerTest {
	
	@Test
	public void should_create_a_cache_only_once() {
		CacheManager cacheManager = mockCacheManager();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table")).isSameAs(cacheManager.fetchByQuery("SELECT * FROM table"));
		assertThat(cacheManager.tableIndex()).isSameAs(cacheManager.tableIndex());
	}
	
	@Test
	public void should_cache_query_content() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.fetchByQuery("SELECT * FROM table1").put("testIndex1", "testContent1");
		cacheManager.fetchByQuery("SELECT * FROM table2").put("testIndex2", "testContent2");
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table1").get("testIndex1")).isEqualTo("testContent1");
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table2").get("testIndex2")).isEqualTo("testContent2");
		
		cacheManager.tableIndex().put("table1", ImmutableSet.<String>of("SELECT * FROM table1"));
		cacheManager.tableIndex().put("table2", ImmutableSet.<String>of("SELECT * FROM table2"));
		assertThat(cacheManager.tableIndex().get("table1")).isEqualTo(ImmutableSet.<String>of("SELECT * FROM table1"));
		assertThat(cacheManager.tableIndex().get("table2")).isEqualTo(ImmutableSet.<String>of("SELECT * FROM table2"));
	}
	
	// utils
	
	private CacheManager mockCacheManager() {
		CacheProvider cacheProvider = new CacheProvider() {
			@Override
			public Cache tableIndex() {
				return mockCache("cacheIndex");
			}
			
			@Override
			public Cache fetchByQuery(String query) {
				return mockCache("cacheQuery");
			}
		};
		return new CacheManager(cacheProvider);
	}
	
	private Cache mockCache(final String cacheName) {
		return new DefaultCache() {
			@Override
			public int hashCode() {
				return cacheName.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return this == obj;
			}

			@Override
			public String toString() {
				return "CACHE " + cacheName;
			}
		};
	}

}
