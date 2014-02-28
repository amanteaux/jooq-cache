package org.jooq.cache;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

import org.jooq.cache.jdbc.CachedData;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Handle cache instances
 * @author amanteaux
 *
 */
public final class CacheManager {
	
	
	private final LoadingCache<String, Cache> queriesCache;
	private final Cache tableIndex;
	
	public CacheManager(final CacheProvider cacheProvider) {
		this.queriesCache = CacheBuilder
				.newBuilder()
				.build(new CacheLoader<String, Cache>() {
					@Override
					public Cache load(String key) throws Exception {
						return cacheProvider.fetchByQuery(key);
					}
				});
		this.tableIndex = cacheProvider.tableIndex();
	}


	// API
	
	/**
	 * @param query a {@link String} query, for example : "SELECT * FROM table WHERE field = ?"
	 * @param queryParameters the parameters list for the query; for a same query, the parameters should always be in the same order
	 * @return The cached query result if it exists, null else
	 */
	public final CachedData getCachedDataIfPresent(String query, List<Object> queryParameters) {
		return (CachedData) fetchByQuery(query).get(joinParameters(queryParameters));
	}
	
	/**
	 * Cache a query result
	 * @param referencedTables the {@link java.util.Set} of tables referenced by the query; for example "SELECT * FROM table2 t2 JOIN table1 t1 ON t2.a=t1.a" referenced "table2" and "table1"
	 * @param query a {@link String} query, for example : "SELECT * FROM table WHERE field = ?"
	 * @param queryParameters the parameters list for the query; for a same query, the parameters should always be in the same order
	 * @param cachedData the query execution result fully loaded, it can be used when the {@link ResultSet} of the query is closed
	 */
	public final void cacheQueryResult(Set<String> referencedTables, String query, List<Object> queryParameters, CachedData cachedData) {
		fetchByQuery(query).put(joinParameters(queryParameters), cachedData);
		index(tableIndex(), referencedTables, query);
	}
	
	/**
	 * Clear the cache for a query
	 * @param query a {@link String} query, for example : "SELECT * FROM table WHERE field = ?"
	 */
	public final void clearByQuery(String query) {
		fetchByQuery(query).clear();
	}
	
	/**
	 * Clear the cache for a table name
	 * @param tableName a {@link String} table name, for example "tableA" or "user"
	 */
	@SuppressWarnings("unchecked")
	public final void clearByTable(String tableName) {
		Set<String> queries = (Set<String>) tableIndex().get(tableName);
		if(queries != null) {
			for(String query : queries) {
				clearByQuery(query);
			}
		}
	}
	
	// internal
	
	/**
	 * @param queryParameters the parameters list for the query; for a same query, the parameters should always be in the same order
	 * @return a {@link String} representation of a parameters list
	 */
	final String joinParameters(List<Object> queryParameters) {
		return queryParameters.toString();
	}
	
	/**
	 * The query cache associate the query parameters to the query result.<br/>
	 * The query parameters is a {@link String} representation of a parameters list<br/>
	 * A query result is represented by {@link CachedData}
	 * @param query a String query, for example : "SELECT * FROM table WHERE field = ?"
	 * @return The cache object for the query
	 */
	final Cache fetchByQuery(String query) {
		try {
			return queriesCache.get(query);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * A table index associate a table name to a list of queries.<br/>
	 * The cache object can be seen as a {@link java.util.Map} with String entries and {@link java.util.Set} of String values.<br/>
	 * For example, the table "table1" will be associated to "SELECT a FROM table1" and "SELECT a FROM table1 WHERE b = ?", but not "SELECT * FROM table2"
	 * @return The table index
	 */
	final Cache tableIndex() {
		return tableIndex;
	}
	
	/**
	 * Add to the index cache the tables referenced in a query
	 * @param tableIndexes The table index which will be updated
	 * @param referencedTables The tables referenced in the query
	 * @param query a String query, for example : "SELECT * FROM table WHERE field = ?"
	 */
	final void index(Cache tableIndexes, Set<String> referencedTables, String query) {
		for(String tableReference : referencedTables) {
			index(tableIndexes, tableReference, query);
		}
	}
	
	/**
	 * Add to the index cache a table referenced in a query
	 * @param tableIndexes The table index which will be updated
	 * @param tableReference A table referenced in the query
	 * @param query a String query, for example : "SELECT * FROM table WHERE field = ?"
	 */
	@SuppressWarnings("unchecked")
	final void index(Cache tableIndexes, String tableReferenceName, String query) {
		if(tableIndexes.get(tableReferenceName) == null) {
			// the synchronized is mandatory else a link can be overridden
			synchronized (tableIndexes) {
				if(tableIndexes.get(tableReferenceName) == null) {
					CopyOnWriteArraySet<String> links = new CopyOnWriteArraySet<String>();
					links.add(query);
					tableIndexes.put(tableReferenceName, links);
					return;
				}
			}
		}
		
		Set<String> links = (Set<String>) tableIndexes.get(tableReferenceName);
		if(!links.contains(query)) {
			links.add(query);
		}
	}
	
}
