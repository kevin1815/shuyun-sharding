package com.shuyun.sharding.standard.event;

import com.alibaba.druid.pool.DruidDataSource;
import com.shuyun.sharding.standard.DatabaseShardingAlgorithm;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.sql.parser.core.extractor.SQLSegmentsExtractorEngine;
import org.apache.shardingsphere.sql.parser.core.filler.SQLStatementFillerEngine;
import org.apache.shardingsphere.sql.parser.core.parser.SQLAST;
import org.apache.shardingsphere.sql.parser.core.parser.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.core.parser.SQLParserFactory;
import org.apache.shardingsphere.sql.parser.core.rule.registry.ParseRuleRegistry;
import org.apache.shardingsphere.sql.parser.sql.segment.SQLSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.SQLStatement;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NewDataSourceEvent {

    private final String shardingSchemaName;

    private final Map<String, DataSource> dataSourceMap;

    private final Map<String, DataSourceConfiguration> dataSourceConfigurations;

    public NewDataSourceEvent(String shardingSchemaName, Map<String, DataSource> dataSourceMap,
            Map<String, DataSourceConfiguration> dataSourceConfigurations) {
        this.shardingSchemaName = shardingSchemaName;
        this.dataSourceMap = dataSourceMap;
        this.dataSourceConfigurations = dataSourceConfigurations;
    }

    public String getShardingSchemaName() {
        return shardingSchemaName;
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public Map<String, DataSourceConfiguration> getDataSourceConfigurations() {
        return dataSourceConfigurations;
    }

    public static void main(String[] args) throws SQLException {
        String sql = "select * from `t_member` where `tenant_id` = ?";
        String databaseTypeName = "MySQL";
        ParseRuleRegistry parseRuleRegistry = ParseRuleRegistry.getInstance();
        SQLSegmentsExtractorEngine extractorEngine = new SQLSegmentsExtractorEngine();
        SQLStatementFillerEngine fillerEngine =
                new SQLStatementFillerEngine(parseRuleRegistry, databaseTypeName);
        SQLParserEngine parserEngine =
                new SQLParserEngine(ParseRuleRegistry.getInstance(), databaseTypeName, sql);
        SQLAST ast = parserEngine.parse();
        Collection<SQLSegment> sqlSegments = extractorEngine.extract(ast);
        Map<ParserRuleContext, Integer> parameterMarkerIndexes = ast.getParameterMarkerIndexes();
        SQLStatement sqlStatement = fillerEngine.fill(sqlSegments, parameterMarkerIndexes.size(),
                ast.getSqlStatementRule());
        System.out.println(sqlStatement);
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDataSource());
        dataSourceMap.put("qiushi6", dataSource_qiushi6());
        TableRuleConfiguration tableRuleConfiguration =
                new TableRuleConfiguration("t_member", "qiushi6.t_member");
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
        // shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(
        // new StandardShardingStrategyConfiguration("tenant_id",
        // new DatabaseShardingAlgorithm()));
        ShardingRule shardingRule =
                new ShardingRule(shardingRuleConfiguration, dataSourceMap.keySet());
        ShardingDataSource shardingDataSource =
                new ShardingDataSource(dataSourceMap, shardingRule, new Properties());
        ShardingConnection shardingConnection = shardingDataSource.getConnection();
        PreparedStatement statement = shardingConnection.prepareStatement(sql);
        statement.setString(1, "qiushi6");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + "," + resultSet.getString(2) + ","
                    + resultSet.getString(3));
        }

    }

    public static DataSource defaultDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(
                "jdbc:mysql://rm-bp18609677n7xree7.mysql.rds.aliyuncs.com:3306/loyalty2_dev?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&useSSL=false");
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

    public static DataSource dataSource_qiushi6() {
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

}
