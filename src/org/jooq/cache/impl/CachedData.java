package org.jooq.cache.impl;

import java.util.List;
import java.util.Map;

public class CachedData {

	private final List<List<Object>> rows;
	private final Map<String, Integer> fields;

	CachedData(List<List<Object>> rows, Map<String, Integer> fields) {
		this.rows = rows;
		this.fields = fields;
	}
	
	public CachedResultSet newResultSet() {
		return new CachedResultSet(this);
	}

	List<List<Object>> getRows() {
		return rows;
	}

	Map<String, Integer> getFields() {
		return fields;
	}

}
