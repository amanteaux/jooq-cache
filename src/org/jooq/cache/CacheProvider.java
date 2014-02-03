package org.jooq.cache;

/**
 * Handle cache objects initialization
 * @author Aurélien Manteaux
 *
 */
public interface CacheProvider {

	/**
	 * This cache should not expire.
	 * @return A cache instance which will be use to index the links between table and query 
	 */
	Cache tableIndex();
	
	/**
	 * This cache should expire to avoid loading all the database into the cache
	 * @param query
	 * @return A cache instance for a query
	 */
	Cache fetchByQuery(String query);
	
}
