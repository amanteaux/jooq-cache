package org.jooq.cache.jdbc;

/**
 * For the {@link CachedResultSetMetaData} 
 * @author Aurélien Manteaux
 *
 */
public class ColumnInfo {

	private final int precision;
	private final int scale;
	private final String label;
	private final String typeName;
	
	public ColumnInfo(int precision, int scale, String label, String typeName) {
		this.precision = precision;
		this.scale = scale;
		this.label = label;
		this.typeName = typeName;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public int getScale() {
		return scale;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getTypeName() {
		return typeName;
	}

}
