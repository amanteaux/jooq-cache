package org.jooq.impl;

import java.util.Map;

import org.jooq.Configuration;
import org.jooq.ConfigurationExtended;
import org.jooq.ConnectionProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.IdGenerator;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapperProvider;
import org.jooq.SQLDialect;
import org.jooq.SchemaMapping;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;

@SuppressWarnings("deprecation")
public class CachedConfigurationExtended implements ConfigurationExtended {

	private static final long serialVersionUID = 8568789914321518861L;
	
	private final ConfigurationExtended delegate;
	private final CachedConnectionProvider cachedConnectionProvider;

	public CachedConfigurationExtended(ConfigurationExtended delegate) {
		this.delegate = delegate;
		this.cachedConnectionProvider = new CachedConnectionProvider(delegate.connectionProvider());
	}
	
	// the cache flavor works here !
	
	public ConnectionProvider connectionProvider() {
		return cachedConnectionProvider;
	}
	
	// delegated methods (no change)

	public IdGenerator idGenerator() {
		return delegate.idGenerator();
	}

	public Map<Object, Object> data() {
		return delegate.data();
	}

	public Object data(Object key) {
		return delegate.data(key);
	}

	public Object data(Object key, Object value) {
		return delegate.data(key, value);
	}

	public RecordMapperProvider recordMapperProvider() {
		return delegate.recordMapperProvider();
	}

	public RecordListenerProvider[] recordListenerProviders() {
		return delegate.recordListenerProviders();
	}

	public ExecuteListenerProvider[] executeListenerProviders() {
		return delegate.executeListenerProviders();
	}

	public VisitListenerProvider[] visitListenerProviders() {
		return delegate.visitListenerProviders();
	}

	public SchemaMapping schemaMapping() {
		return delegate.schemaMapping();
	}

	public SQLDialect dialect() {
		return delegate.dialect();
	}

	public Settings settings() {
		return delegate.settings();
	}

	public Configuration set(ConnectionProvider newConnectionProvider) {
		return delegate.set(newConnectionProvider);
	}

	public Configuration set(RecordMapperProvider newRecordMapperProvider) {
		return delegate.set(newRecordMapperProvider);
	}

	public Configuration set(
			RecordListenerProvider... newRecordListenerProviders) {
		return delegate.set(newRecordListenerProviders);
	}

	public Configuration set(
			ExecuteListenerProvider... newExecuteListenerProviders) {
		return delegate.set(newExecuteListenerProviders);
	}

	public Configuration set(VisitListenerProvider... newVisitListenerProviders) {
		return delegate.set(newVisitListenerProviders);
	}

	public Configuration set(SQLDialect newDialect) {
		return delegate.set(newDialect);
	}

	public Configuration set(Settings newSettings) {
		return delegate.set(newSettings);
	}

	public Configuration derive() {
		return delegate.derive();
	}

	public Configuration derive(ConnectionProvider newConnectionProvider) {
		return delegate.derive(newConnectionProvider);
	}

	public Configuration derive(RecordMapperProvider newRecordMapperProvider) {
		return delegate.derive(newRecordMapperProvider);
	}

	public Configuration derive(
			RecordListenerProvider... newRecordListenerProviders) {
		return delegate.derive(newRecordListenerProviders);
	}

	public Configuration derive(
			ExecuteListenerProvider... newExecuteListenerProviders) {
		return delegate.derive(newExecuteListenerProviders);
	}

	public Configuration derive(
			VisitListenerProvider... newVisitListenerProviders) {
		return delegate.derive(newVisitListenerProviders);
	}

	public Configuration derive(SQLDialect newDialect) {
		return delegate.derive(newDialect);
	}

	public Configuration derive(Settings newSettings) {
		return delegate.derive(newSettings);
	}

	
}
