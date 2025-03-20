package com.quicksand.bigdata.query.ringbuffer;

import com.quicksand.bigdata.query.services.TaskExecuteService;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * QueryTaskHandler
 *
 * @author xupei
 * @date 2022/8/9
 */
@Slf4j
@Builder
public class QueryTaskHandler
        implements ShareRingBuffer.EventHandler<QueryTaskVO> {

    TaskExecuteService taskExecuteService;

    @Override
    public void onEvent(QueryTaskVO task) {
        taskExecuteService.executeTask(task);
    }

    @Override
    public void finalDeal(ShareRingBuffer.Ctx<QueryTaskVO> ctx) {
        ctx.release();
    }

}
