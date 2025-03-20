package com.quicksand.bigdata.query.services;

import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.vos.QueryTaskVO;

/**
 * QueryTaskService
 *
 * @author xupei
 * @date 2022/8/9
 */
public interface QueryTaskService {

    /**
     * 合并/创建查询
     *
     * @param model 查询参数
     * @return instance of QueryTaskVO
     */
    QueryTaskVO query(QueryReqModel model);

    /**
     * 根据taskId检索task
     *
     * @param id String
     * @return instance of QueryTaskVO
     */
    QueryTaskVO fetchTask(String id);


}
