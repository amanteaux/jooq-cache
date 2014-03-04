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
    // XXX: getCachedDataIfPresent(String query, List<Object> queryParameters) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_return_null_because_no_data_exists() {
		CacheManager cacheManager = mockCacheManager();
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table", ImmutableList.of())).isNull();
	}
	
	@Test
	public void should_return_the_cached_data() {
		CacheManager cacheManager = mockCacheManager();
		CachedData mockCachedData = mockCachedData();
		
		cacheManager.cacheQueryResult(ImmutableSet.of("table"), "SELECT * FROM table", ImmutableList.of(), mockCachedData);
		
		assertThat(cacheManager.getCachedDataIfPresent("SELECT * FROM table", ImmutableList.of())).isEqualTo(mockCachedData);
	}
	
    // --------------------------------------------------------------------------------
    // XXX: cacheQueryResult(...) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_add_a_query_result_to_the_query_cache_and_index_it_in_the_table_index() {
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
	
	// internal
	
    // --------------------------------------------------------------------------------
    // XXX: joinParameters(List<Object> queryParameters) testing
    // --------------------------------------------------------------------------------
	
	@Test
	public void should_concatenate_strings() {
		CacheManager cacheManager = mockCacheManager();
		
		assertThat(cacheManager.joinParameters(ImmutableList.<Object>of("a", "b"))).isEqualTo("[a, b]");
		assertThat(cacheManager.joinParameters(ImmutableList.<Object>of())).isEqualTo("[]");
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
    // XXX: index(Cache tableIndexes, Set<String> referencedTables, String query) testing
    // --------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_add_a_query_to_multiple_table_index() {
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
	public void should_use_a_set_for_the_table_index() {
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
