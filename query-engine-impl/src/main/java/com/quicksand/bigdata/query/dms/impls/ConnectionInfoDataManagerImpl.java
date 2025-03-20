package com.quicksand.bigdata.query.dms.impls;

import com.quicksand.bigdata.query.dbvos.ConnectionInfoDBVO;
import com.quicksand.bigdata.query.dms.ConnectionInfoDataManager;
import com.quicksand.bigdata.query.repos.ConnectionInfoAutoRepo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ConnectionInfoDataManagerImpl
 *
 * @author xupei
 * @date 2022/8/11
 */
@Component
public class ConnectionInfoDataManagerImpl
        implements ConnectionInfoDataManager {

    @Resource
    ConnectionInfoAutoRepo connectionInfoAutoRepo;

    @Override
    public ConnectionInfoDBVO findByFlag(String flag) {
        return connectionInfoAutoRepo.findByFlag(flag);
    }

    @Override
    public ConnectionInfoDBVO saveConnectionInfo(ConnectionInfoDBVO connectionInfo) {
        return connectionInfoAutoRepo.save(connectionInfo);
    }

}
