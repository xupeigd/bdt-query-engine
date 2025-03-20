package com.quicksand.bigdata.query.configs;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * DataSourceCOnfiguration
 * <p>
 * com.quicksand.bigdata.query.engine.configurations
 *
 * @author xupei
 * @date 2020/12/17
 */
@Slf4j
@Configuration
public class DataSourceConfiguration {

    static void configDruid(DruidDataSource druidDataSource, ConnectionPoolPros connectionPoolPros) {
        druidDataSource.setName(connectionPoolPros.getName());
        druidDataSource.setMaxActive(null == connectionPoolPros.getMaxActive() ? 0 : connectionPoolPros.getMaxActive());
        druidDataSource.setMaxWait(null == connectionPoolPros.getMaxWait() ? 0 : connectionPoolPros.getMaxWait());
        druidDataSource.setMinIdle(null == connectionPoolPros.getMinIdle() ? 0 : connectionPoolPros.getMinIdle());
        druidDataSource.setTestWhileIdle(connectionPoolPros.isTestWhileIdle());
        druidDataSource.setInitialSize(null == connectionPoolPros.getInitialSize() ? 0 : connectionPoolPros.getInitialSize());
        druidDataSource.setValidationQuery(connectionPoolPros.getValidationQuery());
        druidDataSource.setFailFast(connectionPoolPros.getFailFast());
        //使用前测试可用性
        druidDataSource.setTestOnBorrow(connectionPoolPros.getTestOnBorrow());
        druidDataSource.setTestOnReturn(connectionPoolPros.getTestOnReturn());
        //最坏的情况，重试最大的池子容量
        druidDataSource.setConnectionErrorRetryAttempts(null == connectionPoolPros.getMaxActive() ? 1 : connectionPoolPros.getMaxActive());
        druidDataSource.setKeepAlive(connectionPoolPros.getKeepAlive());
        druidDataSource.setLoginTimeout(3000L > connectionPoolPros.getMaxWait() ? 3 : ((int) (connectionPoolPros.getMaxWait() / 1000L)));
        druidDataSource.setQueryTimeout(connectionPoolPros.getQueryTimeout());
    }

    @SneakyThrows
    static void configHikari(HikariDataSource dataSource, ConnectionPoolPros connectionPoolPros) {
        dataSource.setPoolName(connectionPoolPros.getName());
        dataSource.setMaximumPoolSize(null == connectionPoolPros.getMaxActive() ? 0 : connectionPoolPros.getMaxActive());
        dataSource.setMinimumIdle(null == connectionPoolPros.getMinIdle() ? 0 : connectionPoolPros.getMinIdle());
        //最大存活时间（ms）
        dataSource.setMaxLifetime(connectionPoolPros.getMaxLifetime());
        //hikari中获取连接超时从未连接开始计算
        dataSource.setConnectionTimeout(null == connectionPoolPros.getConnectionTimeout() ? 3000L : connectionPoolPros.getConnectionTimeout());
        dataSource.setConnectionTestQuery(connectionPoolPros.getValidationQuery());
        //泄漏检测时间
        dataSource.setLeakDetectionThreshold(connectionPoolPros.getLeakDetectionThreshold());
        dataSource.setValidationTimeout(null == connectionPoolPros.getValidationTimeout() ? 10000L : connectionPoolPros.getValidationTimeout());
        dataSource.setIdleTimeout(connectionPoolPros.getIdleTimeout());
        int loginTimeout = 3000L > connectionPoolPros.getMaxWait() ? 3 : ((int) (connectionPoolPros.getMaxWait() / 1000L));
        dataSource.setLoginTimeout(loginTimeout);
        dataSource.setAutoCommit(true);
    }

    public static DataSource configConnectionPool(DataSource dataSource, ConnectionPoolPros connectionPoolPros) {
        if (dataSource instanceof DruidDataSource) {
            configDruid((DruidDataSource) dataSource, connectionPoolPros);
        }
        if (dataSource instanceof HikariDataSource) {
            configHikari((HikariDataSource) dataSource, connectionPoolPros);
        }
        return dataSource;
    }

    @Bean
    @ConfigurationProperties("engine.vars.common.pool")
    public DataSourceConfiguration.ConnectionPoolPros commonPoolPros() {
        return new DataSourceConfiguration.ConnectionPoolPros();
    }

    @Data
    public static class ConnectionPoolPros {
        /**
         * 池子类型
         */
        String type;

        /**
         * 名称
         * （会被覆盖）
         */
        String name;

        /**
         * 最小空闲
         */
        Integer minIdle;

        /**
         * 最大活跃
         */
        Integer maxActive;

        /**
         * 初始数量
         */
        Integer initialSize;

        /**
         * 是否测试空闲连接
         */
        boolean testWhileIdle;

        /**
         * 最大等待时长
         */
        Integer maxWait;

        /**
         * 验证查询语句
         */
        String validationQuery;

        /**
         * 最大存活时间
         * （HikariCP适用）
         */
        Long maxLifetime;

        /**
         * 连接泄漏Detection时间
         * （单位 ms）
         * （HikariCP特有）
         */
        Long leakDetectionThreshold;

        /**
         * 是否FailFast
         * （DruidCP特有）
         */
        Boolean failFast;

        /**
         * 是否keepAlive
         * （DruidCP特有）
         */
        Boolean keepAlive;

        /**
         * 使用前测试
         * （DruidCP特有,牺牲性能保证可用性）
         */
        Boolean testOnBorrow;

        /**
         * 回池前测试
         * （DruidCP特有，牺牲性能保证可用性）
         */
        Boolean testOnReturn;

        /**
         * 查询超时时间
         * (单位 s)
         * （默认 120s）
         */
        Integer queryTimeout;

        /**
         * 验证超时时间
         * （单位 ms）
         * （默认 30000L）
         */
        Long validationTimeout;

        /**
         * 空闲超时时间
         * （单位 ms）
         * （默认 15000）
         */
        Long idleTimeout;

        /**
         * 连接超时时间
         * （单位 ms）
         * （默认 120000）
         */
        Long connectionTimeout;

    }


}