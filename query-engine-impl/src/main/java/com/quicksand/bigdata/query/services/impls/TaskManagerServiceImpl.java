package com.quicksand.bigdata.query.services.impls;

import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;
import com.quicksand.bigdata.query.services.TaskManagerService;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TaskManagerServiceImpl
 *
 * @author xupei
 * @date 2022/8/10
 */
@Service
public class TaskManagerServiceImpl
        implements TaskManagerService {

    @Resource
    RedisTemplate<String, QueryTaskVO> queryTaskRedisTemplate;
    @Resource
    ShareRingBuffer<QueryTaskVO> queryTaskRingBuffer;

    @Override
    public QueryTaskVO findTask(String id) {
        return queryTaskRingBuffer.fetch(id);
    }

}
