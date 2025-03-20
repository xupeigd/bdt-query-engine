package com.quicksand.bigdata.query.configs;

//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * ApolloConfiguaration
 *
 * @author xupei
 * @date 2022/9/14
 */
@Configuration
//@EnableApolloConfig
@ConditionalOnProperty(name = "engine.vars.apollo.enable", havingValue = "true")
public class ApolloConfiguaration {
}
