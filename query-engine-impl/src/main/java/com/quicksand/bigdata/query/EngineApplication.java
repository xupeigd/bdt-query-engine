package com.quicksand.bigdata.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EngineApplication
 *
 * @author xupei
 * @date 2022/8/2
 */
@SpringBootApplication(scanBasePackages = {"com.quicksand.bigdata.query", "com.quicksand.bigdata.vars"})
public class EngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
    }

}
