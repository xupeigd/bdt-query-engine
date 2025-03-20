package com.quicksand.bigdata.query.services.impls.impls;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.query.configs.DataSourceConfiguration;
import com.quicksand.bigdata.query.consts.DsType;
import com.quicksand.bigdata.query.services.DatasourceCacheService;
import com.quicksand.bigdata.query.utils.TraceFuture;
import com.quicksand.bigdata.query.vos.ConnectionInfoVO;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * DatasourceCacheServiceImpl
 *
 * @author xupei
 * @date 2022/8/9
 */
@Slf4j
@Service
public class DatasourceCacheServiceImpl
        implements DatasourceCacheService {

    private static final String protocolTemplate = "jdbc:%s://%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private final LoadingCache<String, CacheDatasource> initedDatasources = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, CacheDatasource>() {
                @Override
                public CacheDatasource load(String key) {
                    return CacheDatasource.VOID;
                }
            });
    @Resource
    DataSourceConfiguration.ConnectionPoolPros commonPoolPros;

    @PostConstruct
    public void init() {
        initCloseWorker();
        initBadDatasourceWorker();
    }

    private void initBadDatasourceWorker() {
        TraceFuture.run(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                Try.run(() -> {
                            List<Map.Entry<String, CacheDatasource>> validationDatasources = null;
                            synchronized (initedDatasources) {
                                validationDatasources = initedDatasources.asMap().entrySet()
                                        .stream()
                                        .filter(v -> !Objects.equals(CacheDatasource.VOID, v.getValue()))
                                        .collect(Collectors.toList());
                            }
                            if (!CollectionUtils.isEmpty(validationDatasources)) {
                                List<Map.Entry<String, CacheDatasource>> badDatasources = validationDatasources.stream()
                                        .filter(v -> v.getValue().badDatasource())
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(badDatasources)) {
                                    synchronized (initedDatasources) {
                                        for (Map.Entry<String, CacheDatasource> entry : badDatasources) {
                                            initedDatasources.invalidate(entry.getKey());
                                        }
                                    }
                                    badDatasources.stream()
                                            .peek(v -> log.info("close bad datasource ! name:{}", v.getKey()))
                                            .forEach(v -> v.getValue().close());
                                }
                            }
                        })
                        .onFailure(ex -> log.error("DatasourceCacheServiceImpl Clean Workers error!", ex))
                        .andFinally(() -> {
                            try {
                                TimeUnit.MINUTES.sleep(1);
                            } catch (InterruptedException e) {
                                log.warn("DatasourceCacheServiceImpl Clean Workers sleep error !", e);
                            }
                        });
            }
        });
    }

    public void initCloseWorker() {
        TraceFuture.run(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                Try.run(() -> {
                            List<Map.Entry<String, CacheDatasource>> timeoutDatssources = null;
                            synchronized (initedDatasources) {
                                timeoutDatssources = initedDatasources.asMap().entrySet()
                                        .stream()
                                        .filter(v -> !Objects.equals(CacheDatasource.VOID, v.getValue()))
                                        .filter(v -> v.getValue().isTimeout(10 * 60 * 1000L))
                                        .collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(timeoutDatssources)) {
                                    for (Map.Entry<String, CacheDatasource> entry : timeoutDatssources) {
                                        initedDatasources.invalidate(entry.getKey());
                                    }
                                }
                            }
                            //关闭数据源
                            if (!CollectionUtils.isEmpty(timeoutDatssources)) {
                                timeoutDatssources.forEach(v -> v.getValue().close());
                            }
                        })
                        .onFailure(ex -> log.error("DatasourceCacheServiceImpl CloseWorkers error!", ex))
                        .andFinally(() -> {
                            try {
                                TimeUnit.MINUTES.sleep(1L);
                            } catch (InterruptedException e) {
                                log.warn("CloseWorkers sleep error !", e);
                            }
                        });
            }
        });


    }

    private String resolveDriveClass(DsType type) {
        return "com.mysql.jdbc.Driver";
    }

    private String resolveProtocolUrl(ConnectionInfoVO connectionInfo) {
        return String.format(protocolTemplate, connectionInfo.getType().getProtocolFlag(), connectionInfo.getAddress(), connectionInfo.getDefaultDatabase());
    }

    private DataSourceProperties cover2Pros(ConnectionInfoVO connectionInfo) {
        DataSourceProperties dsp = new DataSourceProperties();
        dsp.setPassword(connectionInfo.getPassword());
        dsp.setUsername(connectionInfo.getUserName());
        dsp.setName(connectionInfo.getName());
        dsp.setDriverClassName(resolveDriveClass(connectionInfo.getType()));
        dsp.setUrl(resolveProtocolUrl(connectionInfo));
        dsp.setType(StringUtils.hasText(commonPoolPros.getType()) && commonPoolPros.getType().endsWith("HikariDataSource")
                ? HikariDataSource.class
                : DruidDataSource.class);
        return dsp;
    }

    @Override
    public CacheDatasource fetchOrCreateDatasource(ConnectionInfoVO connectionInfo) {
        //判重复
        String infoMd5 = DigestUtils.md5DigestAsHex((JsonUtils.toJsonString(connectionInfo)).getBytes());
        CacheDatasource mayExistDatasource = initedDatasources.getUnchecked(infoMd5);
        if (!Objects.equals(CacheDatasource.VOID, mayExistDatasource)) {
            return mayExistDatasource;
        } else {
            synchronized (initedDatasources) {
                if (Objects.equals(CacheDatasource.VOID, initedDatasources.getUnchecked(infoMd5))) {
                    initedDatasources.put(infoMd5, CacheDatasource.builder()
                            .name(connectionInfo.getName())
                            .infoMd5(infoMd5)
                            .dataSource(
                                    DataSourceConfiguration
                                            .configConnectionPool(cover2Pros(connectionInfo).initializeDataSourceBuilder().build(), commonPoolPros))
                            .createMills(System.currentTimeMillis())
                            .build()
                            .init());
                }
            }
        }
        return initedDatasources.getUnchecked(infoMd5);
    }

}
