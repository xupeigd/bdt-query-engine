package com.quicksand.bigdata.query.services;

import com.alibaba.druid.pool.DruidDataSource;
import com.quicksand.bigdata.query.vos.ConnectionInfoVO;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * DatasourceCacheService
 *
 * @author xupei
 * @date 2022/8/9
 */
public interface DatasourceCacheService {

    /**
     * 构建数据源
     *
     * @param connectionInfo 连接信息
     * @return instance of DataSource
     */
    CacheDatasource fetchOrCreateDatasource(ConnectionInfoVO connectionInfo);

    @Slf4j
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    final class CacheDatasource
            implements DataSource {

        public static final CacheDatasource VOID = new CacheDatasource();

        /**
         * 包装数据源
         */
        DataSource dataSource;

        /**
         * 创建时间
         */
        long createMills = 0L;

        /**
         * 最后调用时间
         * （以nametemplate计算）
         */
        long lastInvokeMills = 0L;

        /**
         * 数据源的名称
         */
        String name;

        /**
         * 数据源配置的Md5
         */
        String infoMd5;

        /**
         * 已构建的JdbcTemplate
         */
        NamedParameterJdbcTemplate namedParameterJdbcTemplate;

        @Override
        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return dataSource.getConnection(username, password);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return dataSource.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return dataSource.isWrapperFor(iface);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return dataSource.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            dataSource.setLogWriter(out);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return dataSource.getLoginTimeout();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            dataSource.setLoginTimeout(seconds);
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return dataSource.getParentLogger();
        }

        public CacheDatasource init() {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            return this;
        }

        public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
            Assert.notNull(namedParameterJdbcTemplate, "数据源未初始化/已销毁！");
            lastInvokeMills = System.currentTimeMillis();
            return namedParameterJdbcTemplate;
        }

        public boolean isTimeout(long mills) {
            return System.currentTimeMillis() - mills > lastInvokeMills;
        }

        public void close() {
            synchronized (this) {
                if (null != dataSource) {
                    if (dataSource instanceof Closeable) {
                        Try.run(() -> ((Closeable) dataSource).close())
                                .onFailure(ex -> log.warn(String.format("CacheDatasource close fail : that seem like leak memory ! name:%s,infoMd5:%s", name, infoMd5), ex));
                    }
                }
                namedParameterJdbcTemplate = null;
                dataSource = null;
            }
            log.info("CacheDatasource close ! name:{},infoMd5:{}", name, infoMd5);
        }

        public boolean badDatasource() {
            if (dataSource instanceof DruidDataSource
                    && 0 == ((DruidDataSource) dataSource).getActiveCount()
                    && 0 < ((DruidDataSource) dataSource).getCreateErrorCount()) {
                return true;
            }
            if (dataSource instanceof HikariDataSource) {
                //supply
            }
            return false;
        }
    }

}
