package org.jooq.cache.jdbc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.impl.CacheQueryInformation;

/**
 * A ResultSet that caches all the data the pass trough.<br/>
 * The cached data are written to the query cache once the ResultSet is closed.
 * 
 * @author Aurélien Manteaux
 * 
 */
class CachingResultSet implements ResultSet {

	private final ResultSet delegate;
	private final CacheQueryInformation queryInformation;

	private final List<Object[]> rows;
	private final Map<String, Integer> fields;
	private final List<ColumnInfo> colsInfo;
	private Object[] row;
	
	private final int cols;

	CachingResultSet(ResultSet delegate, CacheQueryInformation queryInformation) throws SQLException {
		this.delegate = delegate;
		this.queryInformation = queryInformation;

		// fetch the columns meta-data
		ResultSetMetaData metaData = delegate.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>(columnCount);
		Map<String, Integer> fields = new HashMap<String, Integer>(columnCount);
		for (int i = 1; i < columnCount + 1; i++ ) {
			fields.put(metaData.getColumnName(i), i);
			columnInfos.add(new ColumnInfo(metaData.getPrecision(i), metaData.getScale(i), metaData.getColumnLabel(i), metaData.getColumnTypeName(i)));
		}
		this.fields = Collections.unmodifiableMap(fields);
		this.colsInfo = Collections.unmodifiableList(columnInfos);
		this.cols = fields.size();
		
		this.rows = new ArrayList<Object[]>();
		this.row = null;
	}
	
	// utils
	
	private<T> T cache(T value, int columnIndex) {
		row[columnIndex -1] = value;
		return value;
	}
	
	private<T> T cache(T value, String columnLabel) {
		return cache(value, fields.get(columnLabel));
	}
	
	private InputStream cacheAndStream(InputStream toCache, int columnIndex) throws SQLException {
		try {
			return new ByteArrayInputStream(cache(IOUtils.toByteArray(toCache), columnIndex));
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}
	
	private InputStream cacheAndStream(InputStream toCache, String columnLabel) throws SQLException {
		return cacheAndStream(toCache, fields.get(columnLabel));
	}
	
	private Reader cacheAndStream(Reader toCache, int columnIndex) throws SQLException {
		try {
			return new InputStreamReader(new ByteArrayInputStream(cache(IOUtils.toByteArray(toCache), columnIndex)));
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}
	
	private Reader cacheAndStream(Reader toCache, String columnLabel) throws SQLException {
		return cacheAndStream(toCache, fields.get(columnLabel));
	}

	/**
	 * If the current row is not null, add it to the result rows 
	 */
	private void addRowIfNecessary() {
		if(row != null) {
			rows.add(row);
		}
	}
	
	// impl
	
	@Override
	public boolean next() throws SQLException {
		addRowIfNecessary();
		if(!delegate.next()) {
			row = null;
			return false;
		}
		row = new Object[cols];
		return true;
	}

	@Override
	public void close() throws SQLException {
		delegate.close();
		
		addRowIfNecessary();

		queryInformation.getCacheManager().cacheQueryResult(queryInformation.getReferencedTables(), queryInformation.getQuery(), queryInformation.getQueryParameters(), new CachedData(Collections.unmodifiableList(rows), fields, colsInfo));
	}

	@Override
	public boolean wasNull() throws SQLException {
		boolean wasNull = delegate.wasNull();
		if(wasNull) {
			rows.set(rows.size()-1, null);
		}
		
		return wasNull;
	}

	// caching
	
	@Override
	public String getString(int columnIndex) throws SQLException {
		return cache(delegate.getString(columnIndex), columnIndex);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return cache(delegate.getBoolean(columnIndex), columnIndex);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return cache(delegate.getByte(columnIndex), columnIndex);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return cache(delegate.getShort(columnIndex), columnIndex);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return cache(delegate.getInt(columnIndex), columnIndex);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return cache(delegate.getLong(columnIndex), columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return cache(delegate.getFloat(columnIndex), columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return cache(delegate.getDouble(columnIndex), columnIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return cache(delegate.getBigDecimal(columnIndex, scale), columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return cache(delegate.getBytes(columnIndex), columnIndex);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return cache(delegate.getDate(columnIndex), columnIndex);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return cache(delegate.getTime(columnIndex), columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return cache(delegate.getTimestamp(columnIndex), columnIndex);
	}

	
	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return cacheAndStream(delegate.getAsciiStream(columnIndex), columnIndex);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return cacheAndStream(delegate.getUnicodeStream(columnIndex), columnIndex);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return cacheAndStream(delegate.getBinaryStream(columnIndex), columnIndex);
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return cache(delegate.getString(columnLabel), columnLabel);
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return cache(delegate.getBoolean(columnLabel), columnLabel);
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return cache(delegate.getByte(columnLabel), columnLabel);
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return cache(delegate.getShort(columnLabel), columnLabel);
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return cache(delegate.getInt(columnLabel), columnLabel);
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return cache(delegate.getLong(columnLabel), columnLabel);
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return cache(delegate.getFloat(columnLabel), columnLabel);
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return cache(delegate.getDouble(columnLabel), columnLabel);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return cache(delegate.getBigDecimal(columnLabel, scale), columnLabel);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return cache(delegate.getBytes(columnLabel), columnLabel);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return cache(delegate.getDate(columnLabel), columnLabel);
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return cache(delegate.getTime(columnLabel), columnLabel);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return cache(delegate.getTimestamp(columnLabel), columnLabel);
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return cacheAndStream(delegate.getAsciiStream(columnLabel), columnLabel);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return cacheAndStream(delegate.getUnicodeStream(columnLabel), columnLabel);
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return cacheAndStream(delegate.getBinaryStream(columnLabel), columnLabel);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return cache(delegate.getObject(columnIndex), columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return cache(delegate.getObject(columnLabel), columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return cacheAndStream(delegate.getCharacterStream(columnIndex), columnIndex);
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return cacheAndStream(delegate.getCharacterStream(columnLabel), columnLabel);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return cache(delegate.getBigDecimal(columnIndex), columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return cache(delegate.getBigDecimal(columnLabel), columnLabel);
	}
	
	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return cache(delegate.getNClob(columnIndex), columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return cache(delegate.getNClob(columnLabel), columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return cache(delegate.getSQLXML(columnIndex), columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return cache(delegate.getSQLXML(columnLabel), columnLabel);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return cache(delegate.getNString(columnIndex), columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return cache(delegate.getNString(columnLabel), columnLabel);
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return cacheAndStream(delegate.getNCharacterStream(columnIndex), columnIndex);
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return cacheAndStream(delegate.getNCharacterStream(columnLabel), columnLabel);
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return cache(delegate.getRef(columnIndex), columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return cache(delegate.getBlob(columnIndex), columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return cache(delegate.getClob(columnIndex), columnIndex);
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return cache(delegate.getArray(columnIndex), columnIndex);
	}


	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return cache(delegate.getRef(columnLabel), columnLabel);
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return cache(delegate.getBlob(columnLabel), columnLabel);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return cache(delegate.getClob(columnLabel), columnLabel);
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return cache(delegate.getArray(columnLabel), columnLabel);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return cache(delegate.getDate(columnIndex, cal), columnIndex);
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return cache(delegate.getDate(columnLabel, cal), columnLabel);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return cache(delegate.getTime(columnIndex, cal), columnIndex);
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return cache(delegate.getTime(columnLabel, cal), columnLabel);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return cache(delegate.getTimestamp(columnIndex, cal), columnIndex);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return cache(delegate.getTimestamp(columnLabel, cal), columnLabel);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return cache(delegate.getURL(columnIndex), columnIndex);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return cache(delegate.getURL(columnLabel), columnLabel);
	}
	
	// delegate
	
	@Override
	public boolean isClosed() throws SQLException {
		return delegate.isClosed();
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return delegate.findColumn(columnLabel);
	}
	
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return delegate.getMetaData();
	}
	
	// Not implemented (may change)

	
	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		// to implement if needed
		// return delegate.getObject(columnIndex, map);
		throw new RuntimeException("Not implemented");
	}


	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		// to implement if needed
		// return delegate.getObject(columnLabel, map);
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public boolean isBeforeFirst() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isFirst() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isLast() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void beforeFirst() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void afterLast() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean first() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean last() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean previous() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getType() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getConcurrency() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean rowInserted() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void insertRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void deleteRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void refreshRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Statement getStatement() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

}
