package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.jooq.cache.jdbc.CachedData;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class CacheManagerTest {
	
    // --------------------------------------------------------------------------------
    // XXX: index(Cache tableIndexes, String tableReferenceName, String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_use_set_for_table_index() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table");
		
		assertThat(cache.get("table")).isInstanceOf(Set.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_add_a_table_and_a_query_to_the_table_index() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table");
		
		assertThat(cache.get("table")).isNotNull();
		assertThat((Set<String>) cache.get("table")).isEqualTo(ImmutableSet.of("SELECT * FROM table"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_add_a_query_to_the_table_index() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table WHERE a = ?");
		cacheManager.index(cache, "table", "SELECT * FROM table WHERE b = ?");
		
		assertThat((Set<String>) cache.get("table")).isEqualTo(ImmutableSet.of("SELECT * FROM table WHERE a = ?", "SELECT * FROM table WHERE b = ?"));
	}
	
    // --------------------------------------------------------------------------------
    // XXX: tableIndex() and fetchByQuery(String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_create_a_cache_only_once() {
		CacheManager cacheManager = mockCacheManager();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table")).isSameAs(cacheManager.fetchByQuery("SELECT * FROM table"));
		assertThat(cacheManager.tableIndex()).isSameAs(cacheManager.tableIndex());
	}
	
    // --------------------------------------------------------------------------------
    // XXX: clearByQuery(String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_clear_the_cache_for_a_query() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.cacheQueryResult(ImmutableSet.of("table1"), "SELECT * FROM table1", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table2"), "SELECT * FROM table2", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table1", "table2"), "SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of(), mockCachedData());

		cacheManager.clearByQuery("SELECT * FROM table1");
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table1", ImmutableList.of())).isNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2", ImmutableList.of())).isNotNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of())).isNotNull();
	}
	
    // --------------------------------------------------------------------------------
    // XXX: clearByTable(String tableName) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_clear_the_cache_for_a_table_referenced_in_queries() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.cacheQueryResult(ImmutableSet.of("table1"), "SELECT * FROM table1", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table2"), "SELECT * FROM table2", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table1", "table2"), "SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of(), mockCachedData());

		cacheManager.clearByTable("table1");
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table1", ImmutableList.of())).isNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2", ImmutableList.of())).isNotNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of())).isNull();
	}
	
	// utils
	
	private CacheManager mockCacheManager() {
		CacheProvider cacheProvider = new CacheProvider() {
			@Override
			public Cache tableIndex() {
				return new DefaultCache();
			}
			
			@Override
			public Cache fetchByQuery(String query) {
				return new DefaultCache();
			}
		};
		return new CacheManager(cacheProvider);
	}
	
	private CachedData mockCachedData() {
		return new CachedData(ImmutableList.<Object[]>of(), ImmutableMap.<String, Integer>of());
	}
	
}
