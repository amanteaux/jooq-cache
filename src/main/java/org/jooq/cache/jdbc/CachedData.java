package org.jooq.cache.jdbc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Describe a chached query result with its fields and its rows
 * @author Aurélien Manteaux
 *
 */
public class CachedData implements Serializable {

	private static final long serialVersionUID = 1077350001901121366L;
	
	private final List<Object[]> rows;
	private final Map<String, Integer> fields;

	CachedData(List<Object[]> rows, Map<String, Integer> fields) {
		this.rows = rows;
		this.fields = fields;
	}
	
	public CachedResultSet newResultSet() {
		return new CachedResultSet(this);
	}

	List<Object[]> getRows() {
		return rows;
	}

	Map<String, Integer> getFields() {
		return fields;
	}

}
