package com.quicksand.bigdata.query.services.impls;

import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;
import com.quicksand.bigdata.query.services.DatasourceCacheService;
import com.quicksand.bigdata.query.services.TaskExecuteService;
import com.quicksand.bigdata.query.utils.ResultSetColumnResolver;
import com.quicksand.bigdata.query.vos.QueryTaskVO;
import com.quicksand.bigdata.query.vos.ResultSetVO;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * TaskExecuteServiceImpl
 *
 * @author xupei
 * @date 2022/8/11
 */
@Slf4j
@Service
public class TaskExecuteServiceImpl
        implements TaskExecuteService {

    @Resource
    RedisTemplate<String, String> stringRedisTemplate;
    @Resource
    DatasourceCacheService datasourceCacheService;

    @Override
    public void executeTask(QueryTaskVO task) {
        //判重
        String lockKey = String.format("BDT:SRB:ERY:%s:%s:LCK", task.getCurBuffer().getName(), task.getId());
        if (Objects.equals(true, stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, String.format("%s:%d", task.getCurBuffer().getLocalFlag(), System.currentTimeMillis())
                        , 10, TimeUnit.SECONDS))) {
            String taskKey = String.format(ShareRingBuffer.RedisRingBuffer.RDS_ENTRY, task.getCurBuffer().getName(), task.getId());
            QueryTaskVO reloadTask = task.reload();
            if (null == reloadTask
                    || JobState.Cancel.getCode() < reloadTask.getState().getCode()) {
                log.info("executeTask Interrupted ! taskKey:{}", taskKey);
            }
            if (Objects.equals(true, stringRedisTemplate.opsForHash().putIfAbsent(taskKey, "ELOC", task.getCurBuffer().getLocalFlag()))) {
                //放置执行标识
                task.setStartTime(new Date());
                task.persistence();
                Try.run(() -> {
                            ResultSetVO resultSetVO = datasourceCacheService
                                    .fetchOrCreateDatasource(task.getConnectionInfo())
                                    .getNamedParameterJdbcTemplate()
                                    .execute(task.getQueryReq().getTemplateSql(),
                                            task.getQueryReq().getParamters(),
                                            ps -> ResultSetColumnResolver.resolve(ps.executeQuery(), task.getReqControlInfo()));
                            task.setResultSet(resultSetVO);
                            task.setState(JobState.Success);
                        })
                        .onFailure(ex -> {
                            log.error(String.format("TaskExecuteServiceImpl executing error ! key:%s", taskKey), ex);
                            task.setState(JobState.Fail);
                            task.setResultSet(ResultSetVO.builder()
                                    .state(JobState.Fail)
                                    .msg(ex.getMessage())
                                    .build());
                        });
                task.setCompleteTime(new Date());
                task.persistence();
            }
        }
        //todo 已经被其他执行器抢占
    }

}
