package com.shuyun.sharding.standard;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.shuyun.sharding.standard.eventbus.ShuyunShardingOrchestrationEventBus;
import org.apache.shardingsphere.api.config.RuleConfiguration;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.core.constant.ShardingConstant;
import org.apache.shardingsphere.orchestration.internal.registry.ShardingOrchestrationFacade;
import org.apache.shardingsphere.orchestration.internal.registry.state.event.CircuitStateChangedEvent;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.unsupported.AbstractUnsupportedOperationDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.datasource.CircuitBreakerDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.util.DataSourceConverter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractShuyunOrchestrationDataSource extends AbstractUnsupportedOperationDataSource implements AutoCloseable {

    private PrintWriter logWriter = new PrintWriter(System.out);

    private final ShardingOrchestrationFacade shardingOrchestrationFacade;

    private boolean isCircuitBreak;

    private final Map<String, DataSourceConfiguration> dataSourceConfigurations = new LinkedHashMap<>();

    @SuppressWarnings("all")
    public AbstractShuyunOrchestrationDataSource(final ShardingOrchestrationFacade shardingOrchestrationFacade) {
        this.shardingOrchestrationFacade = shardingOrchestrationFacade;
        ShuyunShardingOrchestrationEventBus.getInstance().register(this);
    }

    protected abstract DataSource getDataSource();

    @Override
    public final Connection getConnection() throws SQLException {
        return isCircuitBreak ? new CircuitBreakerDataSource().getConnection() : getDataSource().getConnection();
    }

    @Override
    public final Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection();
    }

    @Override
    public final Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public final void close() throws Exception {
        ((AbstractDataSourceAdapter) getDataSource()).close();
        shardingOrchestrationFacade.close();
    }

    @Subscribe
    public final synchronized void renew(final CircuitStateChangedEvent circuitStateChangedEvent) {
        isCircuitBreak = circuitStateChangedEvent.isCircuitBreak();
    }

    protected final void initShardingOrchestrationFacade() {
        shardingOrchestrationFacade.init();
        dataSourceConfigurations.putAll(shardingOrchestrationFacade.getConfigService().loadDataSourceConfigurations(ShardingConstant.LOGIC_SCHEMA_NAME));
    }

    protected final void initShardingOrchestrationFacade(
            final Map<String, Map<String, DataSourceConfiguration>> dataSourceConfigurations, final Map<String, RuleConfiguration> schemaRuleMap, final Properties props) {
        shardingOrchestrationFacade.init(dataSourceConfigurations, schemaRuleMap, null, props);
        this.dataSourceConfigurations.putAll(dataSourceConfigurations.get(ShardingConstant.LOGIC_SCHEMA_NAME));
    }

    protected final synchronized Map<String, DataSource> getChangedDataSources(final Map<String, DataSource> oldDataSources, final Map<String, DataSourceConfiguration> newDataSources) {
        Map<String, DataSource> result = new LinkedHashMap<>(oldDataSources);
        Map<String, DataSourceConfiguration> modifiedDataSources = getModifiedDataSources(newDataSources);
        result.keySet().removeAll(getDeletedDataSources(newDataSources));
        result.keySet().removeAll(modifiedDataSources.keySet());
        result.putAll(DataSourceConverter.getDataSourceMap(modifiedDataSources));
        result.putAll(DataSourceConverter.getDataSourceMap(getAddedDataSources(newDataSources)));
        return result;
    }

    protected final synchronized Map<String, DataSourceConfiguration> getModifiedDataSources(final Map<String, DataSourceConfiguration> dataSourceConfigurations) {
        Map<String, DataSourceConfiguration> result = new LinkedHashMap<>();
        for (Map.Entry<String, DataSourceConfiguration> entry : dataSourceConfigurations.entrySet()) {
            if (isModifiedDataSource(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private synchronized boolean isModifiedDataSource(final Map.Entry<String, DataSourceConfiguration> dataSourceNameAndConfig) {
        return dataSourceConfigurations.containsKey(dataSourceNameAndConfig.getKey()) && !dataSourceConfigurations.get(dataSourceNameAndConfig.getKey()).equals(dataSourceNameAndConfig.getValue());
    }

    protected final synchronized List<String> getDeletedDataSources(final Map<String, DataSourceConfiguration> dataSourceConfigurations) {
        List<String> result = new LinkedList<>(this.dataSourceConfigurations.keySet());
        result.removeAll(dataSourceConfigurations.keySet());
        return result;
    }

    private synchronized Map<String, DataSourceConfiguration> getAddedDataSources(final Map<String, DataSourceConfiguration> dataSourceConfigurations) {
        return Maps.filterEntries(dataSourceConfigurations, input -> !AbstractShuyunOrchestrationDataSource.this.dataSourceConfigurations.containsKey(input.getKey()));
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    protected ShardingOrchestrationFacade getShardingOrchestrationFacade() {
        return shardingOrchestrationFacade;
    }

    protected Map<String, DataSourceConfiguration> getDataSourceConfigurations() {
        return dataSourceConfigurations;
    }

}
