package com.quicksand.bigdata.query.dms;

import com.quicksand.bigdata.query.dbvos.ConnectionInfoDBVO;

/**
 * ConnectionInfoDataManager
 *
 * @author xupei
 * @date 2022/8/11
 */
public interface ConnectionInfoDataManager {

    ConnectionInfoDBVO findByFlag(String flag);

    ConnectionInfoDBVO saveConnectionInfo(ConnectionInfoDBVO connectionInfo);

}
