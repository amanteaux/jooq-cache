package org.jooq.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache backed with a {@link ConcurrentHashMap}
 * @author Aurélien Manteaux
 *
 * @param <K>
 * @param <V>
 */
public class DefaultCache<K, V> implements Cache<K, V> {
	
	private final Map<K, V> data = new ConcurrentHashMap<K, V>();

	@Override
	public boolean contains(K key) {
		return data.containsKey(key);
	}

	@Override
	public V put(K key, V value) {
		data.put(key, value);
		return value;
	}

	@Override
	public V get(K key) {
		return data.get(key);
	}

}
