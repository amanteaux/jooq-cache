package org.jooq.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache backed with a {@link ConcurrentHashMap}
 * @author Aurélien Manteaux
 *
 * @param <K>
 * @param <V>
 */
public class DefaultCache implements Cache {
	
	private final Map<String, Serializable> data = new ConcurrentHashMap<String, Serializable>();

	@Override
	public Serializable put(String key, Serializable value) {
		data.put(key, value);
		return value;
	}

	@Override
	public Serializable get(String key) {
		return data.get(key);
	}

	@Override
	public void clear() {
		data.clear();
	}

}
