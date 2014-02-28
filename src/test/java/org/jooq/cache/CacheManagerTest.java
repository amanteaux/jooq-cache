package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class CacheManagerTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_add_a_table_and_a_query_to_the_cache() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		cacheManager.index(cache, "table_referenced", "select * from test");
		assertThat(cache.get("table_referenced")).isNotNull();
		assertThat((Set<String>) cache.get("table_referenced")).isEqualTo(Sets.newHashSet("select * from test"));
		
		// TODO put that in another specific test
		assertThat(cache.get("table_referenced")).isInstanceOf(Set.class);
	}
	
	
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
	
	@Test
	public void should_clear_the_cache_for_only_a_query() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.fetchByQuery("SELECT * FROM table1").put("testIndex1", "testContent1");
		cacheManager.fetchByQuery("SELECT * FROM table2").put("testIndex2", "testContent2");
		
		cacheManager.clearByQuery("SELECT * FROM table1");
		
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table1").get("testIndex1")).isNull();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table2").get("testIndex2")).isEqualTo("testContent2");
	}
	
	@Test
	public void should_clear_the_cache_for_a_table_referenced_in_queries() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.fetchByQuery("SELECT * FROM table1").put("testIndex1", "testContent1");
		cacheManager.fetchByQuery("SELECT * FROM table2").put("testIndex2", "testContent2");
		cacheManager.fetchByQuery("SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a").put("testIndex3", "testContent3");
		cacheManager.tableIndex().put("table1", ImmutableSet.<String>of("SELECT * FROM table1", "SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a"));
		
		cacheManager.clearByTable("table1");
		
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table1").get("testIndex1")).isNull();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a").get("testIndex3")).isNull();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table2").get("testIndex2")).isEqualTo("testContent2");
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
