package org.jooq.cache.jdbc;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.jooq.cache.DefaultCache;
import org.junit.Test;

import com.google.common.collect.Sets;

public class UtilsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void should_add_a_table_and_a_query_to_the_cache() {
		DefaultCache cache = new DefaultCache();
		Utils.index(cache, "table_referenced", "select * from test");
		assertThat(cache.get("table_referenced")).isNotNull();
		assertThat((Set<String>) cache.get("table_referenced")).isEqualTo(Sets.newHashSet("select * from test"));
		
		// TODO put that in another specific test
		assertThat(cache.get("table_referenced")).isInstanceOf(Set.class);
	}
	
}
