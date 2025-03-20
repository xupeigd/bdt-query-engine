package com.quicksand.bigdata.query.advices;

import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * EnvChecker
 *
 * @author xupei
 * @date 2022/9/14
 */
@Slf4j
@Component
public class EnvChecker
        implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${spring.profiles.active:}")
    String activeProfiles;
    @Value("${spring.redis.host}")
    String redisHost;
    @Value("${spring.redis.port}")
    int redisPort;
    @Value("${spring.redis.database}")
    int redisDb;
    @Value("${spring.datasource.url}")
    String mysqlUrl;
    @Value("${spring.datasource.username}")
    String mysqlUserName;
    @Value("${spring.datasource.password}")
    String mysqlPassword;
    @Value("${query.var.env.checks:false}")
    boolean enableChecker;
    @Value("${apollo.bootstrap.namespaces}")
    String apolloNamespaces;

    @Resource
    RedisTemplate<String, Long> longValueRedistemplate;
    @Resource
    DataSource dataSource;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("-- config namespaces:{}", apolloNamespaces);
        if (enableChecker) {
            testCodis();
            testMysql();
        }
    }

    private void testCodis() {
        log.info("-- EnvChecker testCodis start! --");
        log.info("-- host:{}", redisHost);
        log.info("-- port:{}", redisPort);
        log.info("-- db:{}", redisDb);
        Try.run(() -> {
                    String testKey = String.format("EC:%d", System.currentTimeMillis());
                    log.info("== testKey:{}", testKey);
                    longValueRedistemplate.opsForValue().set(testKey, System.currentTimeMillis(), 1, TimeUnit.MINUTES);
                    log.info("== value:{}", longValueRedistemplate.opsForValue().get(testKey));
                    log.info("== ttl:{}", longValueRedistemplate.getExpire(testKey));
                })
                .onFailure(ex -> log.error("xx testCodis error!", ex));
        log.info("-- EnvChecker testCodis end! --");
    }

    private void testMysql() {
        log.info("-- EnvChecker testMysql start! --");
        log.info("-- url:{}", mysqlUrl);
        log.info("-- userName:{}", mysqlUserName);
        log.info("-- password:{}", mysqlPassword);
        Try.run(() -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    jdbcTemplate.queryForList("show tables")
                            .forEach(v -> log.info("== value:{}", JsonUtils.toJsonString(v)));
                })
                .onFailure(ex -> log.error("xx testCodis error!", ex));
        log.info("-- EnvChecker testMysql end! --");
    }

}
