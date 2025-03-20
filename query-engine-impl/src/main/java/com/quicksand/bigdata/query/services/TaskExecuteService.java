package com.quicksand.bigdata.query.services;

import com.quicksand.bigdata.query.vos.QueryTaskVO;

/**
 * TaskExecuteService
 *
 * @author xupei
 * @date 2022/8/11
 */
public interface TaskExecuteService {

    /**
     * 执行task
     *
     * @param task instance of QueryTaskVO
     */
    void executeTask(QueryTaskVO task);

}
