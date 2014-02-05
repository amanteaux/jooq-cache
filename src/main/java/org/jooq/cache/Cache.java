package org.jooq.cache;

import java.io.Serializable;

/**
 * A minimal cache. All operation must be thread-safe.
 * @author amanteaux
 */
public interface Cache {
	
	/**
	 * Put or replace an entry to the cache
	 * @param key
	 * @param value must not be null
	 * @return The value added to the cache
	 */
	Serializable put(String key, Serializable value);
	
	/**
	 * @param key
	 * @return The value corresponding to the key or null if no value exists for the key
	 */
	Serializable get(String key);
	
	/**
	 * Empty all the values in the cache
	 */
	void clear();

}
