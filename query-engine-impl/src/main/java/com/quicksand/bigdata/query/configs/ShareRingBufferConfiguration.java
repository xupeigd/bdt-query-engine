package com.quicksand.bigdata.query.configs;

import com.quicksand.bigdata.query.ringbuffer.QueryTaskHandler;
import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;
import com.quicksand.bigdata.query.services.TaskExecuteService;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * ShareRingBufferConfiguration
 *
 * @author xupei
 * @date 2022/8/9
 */
@Configuration
public class ShareRingBufferConfiguration {

    @Resource
    RedisTemplate<String, QueryTaskVO> queryTaskRedisTemplate;
    @Resource
    RedisTemplate<String, String> stringRedisTemplate;
    @Resource
    RedisTemplate<String, Long> longRedisTemplate;
    @Resource
    TaskExecuteService taskExecuteService;

    @Bean
    ShareRingBuffer<QueryTaskVO> queryTaskRingBuffer() throws InterruptedException {
        ShareRingBuffer.RedisRingBuffer<QueryTaskVO> shareBuffer = new ShareRingBuffer.RedisRingBuffer<>();
        shareBuffer.setRedisTemplate(queryTaskRedisTemplate);
        shareBuffer.setStringRedisTemplate(stringRedisTemplate);
        shareBuffer.setLongRedisTemplate(longRedisTemplate);
        QueryTaskHandler[] handlers = new QueryTaskHandler[8];
        for (int i = 0; i < 8; i++) {
            handlers[i] = QueryTaskHandler.builder().taskExecuteService(taskExecuteService).build();
        }
        shareBuffer.registHandlers(handlers);
        return shareBuffer.init("QTS", 2048);
    }

}
