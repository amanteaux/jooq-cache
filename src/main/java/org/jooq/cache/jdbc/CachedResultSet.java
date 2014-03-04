package org.jooq.cache.jdbc;

import java.io.ByteArrayInputStream;
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
import java.util.Calendar;
import java.util.Map;

class CachedResultSet implements ResultSet {
	
	private final CachedData cachedData;
	private int rowIndex;
	private Object[] currentRow;
	
	private boolean close;
	private int lastRead;
	
	CachedResultSet(CachedData cachedData) {
		this.cachedData = cachedData;
		this.rowIndex = 0;
		this.close = false;
		this.lastRead = 0;
	}
	
	@Override
	public boolean next() throws SQLException {
		if(rowIndex >= cachedData.getRows().size()) {
			return false;
		}
		currentRow = cachedData.getRows().get(rowIndex++);
		return true;
	}
	
	private Object get(int columnIndex) {
		lastRead = columnIndex;
		return currentRow[columnIndex - 1];
	}
	
	private Object get(String columnLabel) {
		return get(cachedData.getFields().get(columnLabel));
	}
	
	// impl

	@Override
	public void close() throws SQLException {
		close = true;
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		return close;
	}

	@Override
	public boolean wasNull() throws SQLException {
		return get(lastRead) == null;
	}
	
	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return cachedData.getFields().get(columnLabel);
	}
	
	
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new CachedResultSetMetaData(cachedData.getColumnInfos());
	}
	
	// data
	
	@Override
	public String getString(int columnIndex) throws SQLException {
		return (String) get(columnIndex);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return (Boolean) get(columnIndex);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return (Byte) get(columnIndex);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return (Short) get(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return (Integer) get(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return (Long) get(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return (Float) get(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return (Double) get(columnIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return (BigDecimal) get(columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return (byte[]) get(columnIndex);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return (Date) get(columnIndex);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return (Time) get(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return (Timestamp) get(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnIndex));
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnIndex));
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnIndex));
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return (String) get(columnLabel);
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return (Boolean) get(columnLabel);
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return (Byte) get(columnLabel);
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return (Short) get(columnLabel);
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return (Integer) get(columnLabel);
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return (Long) get(columnLabel);
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return (Float) get(columnLabel);
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return (Double) get(columnLabel);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return (BigDecimal) get(columnLabel);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return (byte[]) get(columnLabel);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return (Date) get(columnLabel);
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return (Time) get(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return (Timestamp) get(columnLabel);
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnLabel));
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnLabel));
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return new ByteArrayInputStream((byte[]) get(columnLabel));
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return get(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return get(columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return new InputStreamReader(new ByteArrayInputStream((byte[]) get(columnIndex)));
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return new InputStreamReader(new ByteArrayInputStream((byte[]) get(columnLabel)));
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return (BigDecimal) get(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return (BigDecimal) get(columnLabel);
	}
	
	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return (NClob) get(columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return (NClob) get(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return (SQLXML) get(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return (SQLXML) get(columnLabel);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return (String) get(columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return (String) get(columnLabel);
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return new InputStreamReader(new ByteArrayInputStream((byte[]) get(columnIndex)));
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return new InputStreamReader(new ByteArrayInputStream((byte[]) get(columnLabel)));
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return (Ref) get(columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return (Blob) get(columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return (Clob) get(columnIndex);
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return (Array) get(columnIndex);
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return (Ref) get(columnLabel);
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return (Blob) get(columnLabel);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return (Clob) get(columnLabel);
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return (Array) get(columnLabel);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Date) get(columnIndex);
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Date) get(columnLabel);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Time) get(columnIndex);
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Time) get(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Timestamp) get(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		// the calendar has already been applied during the caching
		return (Timestamp) get(columnLabel);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return (URL) get(columnIndex);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return (URL) get(columnLabel);
	}

	// Not implemented (may change)
	
	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		// to implement if needed
		throw new RuntimeException("Not implemented");
	}


	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		// to implement if needed
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
