package com.quicksand.bigdata.query.services;

import com.quicksand.bigdata.query.vos.QueryTaskVO;

/**
 * TaskManagerService
 *
 * @author xupei
 * @date 2022/8/10
 */
public interface TaskManagerService {

    /**
     * 直接从远端获取。不从本地取
     *
     * @param id taskId
     * @return instance of QueryTaskVO / null
     */
    QueryTaskVO findTask(String id);

}
