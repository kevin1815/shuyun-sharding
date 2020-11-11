package com.shuyun.sharding.standard;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.shuyun.sharding.standard.event.NewDataSourceEvent;
import org.apache.shardingsphere.api.config.RuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.core.constant.ShardingConstant;
import org.apache.shardingsphere.core.rule.MasterSlaveRule;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.orchestration.config.OrchestrationConfiguration;
import org.apache.shardingsphere.orchestration.internal.registry.ShardingOrchestrationFacade;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.DataSourceChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.PropertiesChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.ShardingRuleChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.service.ConfigurationService;
import org.apache.shardingsphere.orchestration.internal.registry.state.event.DisabledStateChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.state.schema.OrchestrationShardingSchema;
import org.apache.shardingsphere.orchestration.internal.rule.OrchestrationMasterSlaveRule;
import org.apache.shardingsphere.orchestration.internal.rule.OrchestrationShardingRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.util.DataSourceConverter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ShuyunOrchestrationShardingDataSource extends AbstractShuyunOrchestrationDataSource {

    private ShardingDataSource dataSource;

    public ShuyunOrchestrationShardingDataSource(final OrchestrationConfiguration orchestrationConfig) throws SQLException {
        super(new ShardingOrchestrationFacade(orchestrationConfig, Collections.singletonList(ShardingConstant.LOGIC_SCHEMA_NAME)));
        ConfigurationService configService = getShardingOrchestrationFacade().getConfigService();
        ShardingRuleConfiguration shardingRuleConfig = configService.loadShardingRuleConfiguration(ShardingConstant.LOGIC_SCHEMA_NAME);
        Preconditions.checkState(!shardingRuleConfig.getTableRuleConfigs().isEmpty(), "Missing the sharding rule configuration on registry center");
        Map<String, DataSourceConfiguration> dataSourceConfigurations = configService.loadDataSourceConfigurations(ShardingConstant.LOGIC_SCHEMA_NAME);
        dataSource = new ShardingDataSource(DataSourceConverter.getDataSourceMap(dataSourceConfigurations), new OrchestrationShardingRule(shardingRuleConfig, dataSourceConfigurations.keySet()),
                configService.loadProperties());
        initShardingOrchestrationFacade();
    }

    public ShuyunOrchestrationShardingDataSource(final ShardingDataSource shardingDataSource, final OrchestrationConfiguration orchestrationConfig) throws SQLException {
        super(new ShardingOrchestrationFacade(orchestrationConfig, Collections.singletonList(ShardingConstant.LOGIC_SCHEMA_NAME)));
        this.dataSource = shardingDataSource;
        initShardingOrchestrationFacade(Collections.singletonMap(ShardingConstant.LOGIC_SCHEMA_NAME, DataSourceConverter.getDataSourceConfigurationMap(dataSource.getDataSourceMap())),
                getRuleConfigurationMap(), dataSource.getRuntimeContext().getProps().getProps());
    }

    private Map<String, RuleConfiguration> getRuleConfigurationMap() {
        Map<String, RuleConfiguration> result = new LinkedHashMap<>(1, 1);
        result.put(ShardingConstant.LOGIC_SCHEMA_NAME, dataSource.getRuntimeContext().getRule().getRuleConfiguration());
        return result;
    }

    @Subscribe
    public final synchronized void renew(final ShardingRuleChangedEvent shardingRuleChangedEvent) {
        try {
            dataSource = new ShardingDataSource(dataSource.getDataSourceMap(), new OrchestrationShardingRule(shardingRuleChangedEvent.getShardingRuleConfiguration(),
                    dataSource.getDataSourceMap().keySet()), dataSource.getRuntimeContext().getProps().getProps());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public final synchronized void renew(NewDataSourceEvent newDataSourceEvent) {
        try {
            Map<String, DataSourceConfiguration> dataSourceConfigurations = newDataSourceEvent.getDataSourceConfigurations();
            Map<String, DataSource> dataSourceMap = this.dataSource.getDataSourceMap();
            dataSourceMap.putAll(newDataSourceEvent.getDataSourceMap());
            getDataSourceConfigurations().putAll(dataSourceConfigurations);
            ShardingRule shardingRule = this.dataSource.getRuntimeContext().getRule();
            Properties properties = this.dataSource.getRuntimeContext().getProps().getProps();
            this.dataSource = new ShardingDataSource(dataSourceMap, shardingRule, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public final synchronized void renew(final DataSourceChangedEvent dataSourceChangedEvent) {
        System.out.println("Receive DataSourceChangedEvent");
        try {
            Map<String, DataSourceConfiguration> dataSourceConfigurations = dataSourceChangedEvent.getDataSourceConfigurations();
//            dataSource.close(getDeletedDataSources(dataSourceConfigurations));
//            dataSource.close(getModifiedDataSources(dataSourceConfigurations).keySet());
//            dataSource = new ShardingDataSource(getChangedDataSources(dataSource.getDataSourceMap(), dataSourceConfigurations),
//                    dataSource.getRuntimeContext().getRule(), dataSource.getRuntimeContext().getProps().getProps());
            getDataSourceConfigurations().clear();
            getDataSourceConfigurations().putAll(dataSourceConfigurations);
            System.out.println("Finished renew datasource");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public final synchronized void renew(final PropertiesChangedEvent propertiesChangedEvent) {
        try {
            dataSource = new ShardingDataSource(dataSource.getDataSourceMap(), dataSource.getRuntimeContext().getRule(), propertiesChangedEvent.getProps());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public synchronized void renew(final DisabledStateChangedEvent disabledStateChangedEvent) {
        OrchestrationShardingSchema shardingSchema = disabledStateChangedEvent.getShardingSchema();
        if (ShardingConstant.LOGIC_SCHEMA_NAME.equals(shardingSchema.getSchemaName())) {
            for (MasterSlaveRule each : dataSource.getRuntimeContext().getRule().getMasterSlaveRules()) {
                ((OrchestrationMasterSlaveRule) each).updateDisabledDataSourceNames(shardingSchema.getDataSourceName(), disabledStateChangedEvent.isDisabled());
            }
        }
    }

    @Override
    protected DataSource getDataSource() {
        return this.dataSource;
    }

}
