package org.jooq.cache;

/**
 * A minimal cache. All operation must be thread-safe.
 * @author amanteaux
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
	
	/**
	 * @param key
	 * @return True if a value exists for the key, else false
	 */
	boolean contains(K key);
	
	/**
	 * Put or replace an entry to the cache
	 * @param key
	 * @param value
	 * @return The value added to the cache
	 */
	V put(K key, V value);
	
	/**
	 * @param key
	 * @return The value corresponding to the key or null if no value exists for the key
	 */
	V get(K key);

}
