package com.shuyun.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MainConfiguration {

    @Bean
    public DataSource defaultDataSource() {
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

    @Bean
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

    //
    @Bean
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

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("shardingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
