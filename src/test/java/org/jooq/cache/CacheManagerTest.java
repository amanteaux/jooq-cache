package org.jooq.cache;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.jooq.cache.impl.DefaultCache;
import org.jooq.cache.jdbc.CachedData;
import org.jooq.cache.jdbc.ColumnInfo;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class CacheManagerTest {

	// --------------------------------------------------------------------------------
    // XXX: getCachedDataIfPresent(String query, List<Object> queryParameters) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_get_null_if_no_data_exists() {
		CacheManager cacheManager = mockCacheManager();
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table", ImmutableList.of())).isNull();
	}
	
	@Test
	public void should_get_cached_data_if_it_has_been_put_to_cache() {
		CacheManager cacheManager = mockCacheManager();
		CachedData mockCachedData = mockCachedData();
		
		cacheManager.cacheQueryResult(ImmutableSet.of("table"), "SELECT * FROM table", ImmutableList.of(), mockCachedData);
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table", ImmutableList.of())).isEqualTo(mockCachedData);
	}
	
    // --------------------------------------------------------------------------------
    // XXX: cacheQueryResult(...) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_add_a_query_result_to_the_query_cache_and_index_it_in_the_table_index_if_a_query_result_is_cached() {
		CacheManager cacheManager = mockCacheManager();
		CachedData mockCachedData = mockCachedData();
		
		cacheManager.cacheQueryResult(ImmutableSet.of("table"), "SELECT * FROM table", ImmutableList.of(), mockCachedData);
		
		assertThat(cacheManager.tableIndex().get("table")).isEqualTo(ImmutableSet.of("SELECT * FROM table"));
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table").get(cacheManager.joinParameters(ImmutableList.of()))).isSameAs(mockCachedData);
	}
	
	
    // --------------------------------------------------------------------------------
    // XXX: clearByQuery(String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void testClearByQuery() {
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
	public void testClearByTable() {
		CacheManager cacheManager = mockCacheManager();
		cacheManager.cacheQueryResult(ImmutableSet.of("table1"), "SELECT * FROM table1", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table2"), "SELECT * FROM table2", ImmutableList.of(), mockCachedData());
		cacheManager.cacheQueryResult(ImmutableSet.of("table1", "table2"), "SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of(), mockCachedData());

		cacheManager.clearByTable("table1");
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table1", ImmutableList.of())).isNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2", ImmutableList.of())).isNotNull();
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a", ImmutableList.of())).isNull();
	}
	
	// internal
	
    // --------------------------------------------------------------------------------
    // XXX: joinParameters(List<Object> queryParameters) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void testJoinParameters() {
		CacheManager cacheManager = mockCacheManager();
		
		assertThat(cacheManager.joinParameters(ImmutableList.<Object>of("a", "b"))).isEqualTo("[a, b]");
		assertThat(cacheManager.joinParameters(ImmutableList.<Object>of())).isEqualTo("[]");
	}
	
    // --------------------------------------------------------------------------------
    // XXX: tableIndex() and fetchByQuery(String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void check_that_a_cache_is_created_only_once_for_a_cacheManager() {
		CacheManager cacheManager = mockCacheManager();
		assertThat(cacheManager.fetchByQuery("SELECT * FROM table")).isSameAs(cacheManager.fetchByQuery("SELECT * FROM table"));
		assertThat(cacheManager.tableIndex()).isSameAs(cacheManager.tableIndex());
	}

    // --------------------------------------------------------------------------------
    // XXX: index(Cache tableIndexes, Set<String> referencedTables, String query) testing
    // --------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIndex() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, ImmutableSet.of("table1", "table2"), "SELECT * FROM table WHERE a = ?");
		
		assertThat((Set<String>) cache.get("table1")).isEqualTo(ImmutableSet.of("SELECT * FROM table WHERE a = ?"));
		assertThat((Set<String>) cache.get("table2")).isEqualTo(ImmutableSet.of("SELECT * FROM table WHERE a = ?"));
	}
	
    // --------------------------------------------------------------------------------
    // XXX: index(Cache tableIndexes, String tableReferenceName, String query) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void check_that_index_values_are_actually_sets() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table");
		
		assertThat(cache.get("table")).isInstanceOf(Set.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void check_that_index_add_both_an_entry_in_the_query_cache_and_in_the_table_index() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table");
		
		assertThat(cache.get("table")).isNotNull();
		assertThat((Set<String>) cache.get("table")).isEqualTo(ImmutableSet.of("SELECT * FROM table"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void check_that_index_use_the_existing_index_if_it_exists() {
		CacheManager cacheManager = mockCacheManager();
		DefaultCache cache = new DefaultCache();
		
		cacheManager.index(cache, "table", "SELECT * FROM table WHERE a = ?");
		cacheManager.index(cache, "table", "SELECT * FROM table WHERE b = ?");
		
		assertThat((Set<String>) cache.get("table")).isEqualTo(ImmutableSet.of("SELECT * FROM table WHERE a = ?", "SELECT * FROM table WHERE b = ?"));
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
		return new CachedData(ImmutableList.<Object[]>of(), ImmutableMap.<String, Integer>of(), ImmutableList.<ColumnInfo>of());
	}
	
}
