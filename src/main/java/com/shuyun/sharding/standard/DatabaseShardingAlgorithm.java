package com.shuyun.sharding.standard;

import com.alibaba.druid.pool.DruidDataSource;
import com.shuyun.sharding.standard.event.NewDataSourceEvent;
import com.shuyun.sharding.standard.eventbus.ShuyunShardingOrchestrationEventBus;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.orchestration.internal.eventbus.ShardingOrchestrationEventBus;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.DataSourceChangedEvent;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Override
    public String doSharding(Collection<String> availableTargetNames,
            PreciseShardingValue<String> shardingValue) {
        String tenantId = shardingValue.getValue();
        DataSource dataSource = this.dataSourceMap.get(tenantId);
        if (dataSource == null) {
            synchronized (this.dataSourceMap) {
                dataSource = this.dataSourceMap.get(tenantId);
                if (dataSource == null) {
                    dataSource = createDataSource(tenantId);
                    this.dataSourceMap.put(tenantId, dataSource);
                    availableTargetNames.add(tenantId);
                    register(tenantId, dataSource);
                }
            }
        }
        return tenantId;
    }

    @SuppressWarnings("all")
    private void register(String dsName, DataSource dataSource) {
        NewDataSourceEvent event = new NewDataSourceEvent("shardingDataSource",
                getDataSourceMap(dsName, dataSource), getDataSourceConfigurationMap());
        ShuyunShardingOrchestrationEventBus.getInstance().post(event);
    }

    private Map<String, DataSource> getDataSourceMap(String dsName, DataSource dataSource) {
        Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1F);
        dataSourceMap.put(dsName, dataSource);
        return dataSourceMap;
    }

    private Map<String, DataSourceConfiguration> getDataSourceConfigurationMap() {
        Map<String, DataSourceConfiguration> dataSourceConfigurationMap =
                new HashMap<>(this.dataSourceMap.size());
        for (Map.Entry<String, DataSource> entry : this.dataSourceMap.entrySet()) {
            dataSourceConfigurationMap.put(entry.getKey(),
                    DataSourceConfiguration.getDataSourceConfiguration(entry.getValue()));
        }
        return dataSourceConfigurationMap;
    }

    private DataSource createDataSource(String tenantId) {
        if ("qiushi6".equals(tenantId)) {
            return dataSource_qiushi6();
        }
        if ("yangyang".equals(tenantId)) {
            return dataSource_yangyang();
        }
        throw new RuntimeException("Can't create datasource for tenantId " + tenantId);
    }

    public DataSource dataSource_qiushi6() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(
                "jdbc:mysql://rm-bp18609677n7xree7.mysql.rds.aliyuncs.com:3306/lp3_001?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&useSSL=false");
        dataSource.setUsername("xadev");
        dataSource.setPassword("xSz6IhlH69qQ4");
        dataSource.setMinIdle(1);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(5);
        dataSource.setMaxWait(60000L);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        return dataSource;
    }

    public DataSource dataSource_yangyang() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(
                "jdbc:mysql://rm-bp18609677n7xree7.mysql.rds.aliyuncs.com:3306/lp3_002?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&useSSL=false");
        dataSource.setUsername("xadev");
        dataSource.setPassword("xSz6IhlH69qQ4");
        dataSource.setMinIdle(1);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(5);
        dataSource.setMaxWait(60000L);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        return dataSource;
    }

}
