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
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsExtended;

@SuppressWarnings("deprecation")
class CachedQueryConfiguration implements ConfigurationExtended {

	private static final long serialVersionUID = 8568789914321518861L;

	private final ConfigurationExtended delegate;
	private final CachedConnectionProvider cachedConnectionProvider;
	private final VisitListener visitListener;

	CachedQueryConfiguration(ConfigurationExtended delegate, CacheQueryInformation queryInformation) {
		this.delegate = delegate;
		this.cachedConnectionProvider = new CachedConnectionProvider(delegate.connectionProvider(), queryInformation);
		this.visitListener = queryInformation.getVisitListener();
	}

	// the cache flavor works here !

	@Override
	public ConnectionProvider connectionProvider() {
		return cachedConnectionProvider;
	}

	@Override
	public VisitListenerProvider[] visitListenerProviders() {
		return Utils.combine(delegate.visitListenerProviders(), new VisitListenerProvider() {
			@Override
			public VisitListener provide() {
				return visitListener;
			}
		});
	}

	// delegated methods (no change)

	@Override
	public ExecuteListenerProvider[] executeListenerProviders() {
		return delegate.executeListenerProviders();
	}

	@Override
	public IdGenerator idGenerator() {
		return delegate.idGenerator();
	}

	@Override
	public Map<Object, Object> data() {
		return delegate.data();
	}

	@Override
	public Object data(Object key) {
		return delegate.data(key);
	}

	@Override
	public Object data(Object key, Object value) {
		return delegate.data(key, value);
	}

	@Override
	public RecordMapperProvider recordMapperProvider() {
		return delegate.recordMapperProvider();
	}

	@Override
	public RecordListenerProvider[] recordListenerProviders() {
		return delegate.recordListenerProviders();
	}

	@Override
	public SchemaMapping schemaMapping() {
		return delegate.schemaMapping();
	}

	@Override
	public SQLDialect dialect() {
		return delegate.dialect();
	}

	@Override
	public Settings settings() {
		return delegate.settings();
	}

	@Override
	public Configuration set(ConnectionProvider newConnectionProvider) {
		return delegate.set(newConnectionProvider);
	}

	@Override
	public Configuration set(RecordMapperProvider newRecordMapperProvider) {
		return delegate.set(newRecordMapperProvider);
	}

	@Override
	public Configuration set(
			RecordListenerProvider... newRecordListenerProviders) {
		return delegate.set(newRecordListenerProviders);
	}

	@Override
	public Configuration set(
			ExecuteListenerProvider... newExecuteListenerProviders) {
		return delegate.set(newExecuteListenerProviders);
	}

	@Override
	public Configuration set(VisitListenerProvider... newVisitListenerProviders) {
		return delegate.set(newVisitListenerProviders);
	}

	@Override
	public Configuration set(SQLDialect newDialect) {
		return delegate.set(newDialect);
	}

	@Override
	public Configuration set(Settings newSettings) {
		return delegate.set(newSettings);
	}

	@Override
	public Configuration derive() {
		return delegate.derive();
	}

	@Override
	public Configuration derive(ConnectionProvider newConnectionProvider) {
		return delegate.derive(newConnectionProvider);
	}

	@Override
	public Configuration derive(RecordMapperProvider newRecordMapperProvider) {
		return delegate.derive(newRecordMapperProvider);
	}

	@Override
	public Configuration derive(
			RecordListenerProvider... newRecordListenerProviders) {
		return delegate.derive(newRecordListenerProviders);
	}

	@Override
	public Configuration derive(
			ExecuteListenerProvider... newExecuteListenerProviders) {
		return delegate.derive(newExecuteListenerProviders);
	}

	@Override
	public Configuration derive(
			VisitListenerProvider... newVisitListenerProviders) {
		return delegate.derive(newVisitListenerProviders);
	}

	@Override
	public Configuration derive(SQLDialect newDialect) {
		return delegate.derive(newDialect);
	}

	@Override
	public Configuration derive(Settings newSettings) {
		return delegate.derive(newSettings);
	}

	@Override
	public SettingsExtended settingsExtended() {
		return delegate.settingsExtended();
	}

	@Override
	public ConfigurationExtended set(SettingsExtended settingsExtended) {
		return delegate.set(settingsExtended);
	}


}
