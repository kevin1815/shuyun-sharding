package com.shuyun.sharding;

import com.shuyun.sharding.standard.DatabaseShardingAlgorithm;
import com.shuyun.sharding.standard.ShuyunOrchestrationShardingDataSource;
import com.shuyun.sharding.standard.TableShardingAlgorithm;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.orchestration.config.OrchestrationConfiguration;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenterConfiguration;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.OrchestrationShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShardingConfiguration {

    private static final String SHARDING_DATASOURCE_NAME = "shardingDataSource";

    private static final String DEFAULT_DATASOURCE_NAME = "default";

    private static final String SHARDING_COLUMN = "tenant_id";

    @Autowired
    private DataSource defaultDataSource;

    @Autowired
    private DataSource dataSource_qiushi6;

    @Autowired
    private DataSource dataSource_yangyang;

    @Bean
    public DataSource shardingDataSource() {

        Map<String, DataSource> dataSourceMap = new HashMap<>();

        dataSourceMap.put(DEFAULT_DATASOURCE_NAME, defaultDataSource);

        dataSourceMap.put("qiushi6", dataSource_qiushi6);

        dataSourceMap.put("yangyang", dataSource_yangyang);

        ShardingRuleConfiguration shardingRuleConfiguration = getShardingRuleConfiguration();

        RegistryCenterConfiguration registryCenterConfiguration =
                new RegistryCenterConfiguration("zookeeper");
        registryCenterConfiguration.setNamespace("shuyun_db_sharding");
        registryCenterConfiguration.setServerLists("localhost:2181");

        OrchestrationConfiguration orchestrationConfiguration = new OrchestrationConfiguration(
                SHARDING_DATASOURCE_NAME, registryCenterConfiguration, true);

        try {
            ShardingDataSource shardingDataSource = new ShardingDataSource(dataSourceMap,
                    new ShardingRule(shardingRuleConfiguration, dataSourceMap.keySet()),
                    new Properties());
            return new ShuyunOrchestrationShardingDataSource(shardingDataSource,
                    orchestrationConfiguration);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private ShardingRuleConfiguration getShardingRuleConfiguration() {

        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();

        shardingRuleConfiguration.setDefaultDataSourceName(DEFAULT_DATASOURCE_NAME);

        Properties properties = new Properties();
        properties.setProperty("leaf.key", "shuyun_leaf_segment");
        properties.setProperty("leaf.segment.id.initial.value", "10000");
        properties.setProperty("registry.center.type", "zookeeper");
        properties.setProperty("server.list", "localhost:2181");
        properties.setProperty("leaf.segment.step", "100");
        KeyGeneratorConfiguration keyGeneratorConfiguration =
                new KeyGeneratorConfiguration("leaf_segment", "id", properties);

        TableRuleConfiguration tableRuleConfiguration =
                new TableRuleConfiguration("t_schedule_job");
        tableRuleConfiguration.setKeyGeneratorConfig(keyGeneratorConfiguration);

        shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);

        ShardingStrategyConfiguration defaultTableShardingStrategyConfiguration =
                new StandardShardingStrategyConfiguration("job_id", new TableShardingAlgorithm());
        ShardingStrategyConfiguration defaultDatabaseShardingStrategyConfiguration =
                new StandardShardingStrategyConfiguration(SHARDING_COLUMN,
                        new DatabaseShardingAlgorithm());

        shardingRuleConfiguration
                .setDefaultTableShardingStrategyConfig(defaultTableShardingStrategyConfiguration);
        shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(
                defaultDatabaseShardingStrategyConfiguration);

        return shardingRuleConfiguration;
    }

}
