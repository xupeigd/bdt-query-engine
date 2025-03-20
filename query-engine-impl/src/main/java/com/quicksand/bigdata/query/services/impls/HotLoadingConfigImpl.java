package com.quicksand.bigdata.query.services.impls;

import com.quicksand.bigdata.query.services.HotLoadingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * HotLoadingConfigImpl
 *
 * @author xupei
 * @date 2022/8/10
 */
@Service
public class HotLoadingConfigImpl
        implements HotLoadingConfig {

    @Value("${query.sync.query.enable:true}")
    boolean querysyncEnable;
    @Value("${query.sync.query.timeout:60000}")
    long maxSyncMills;


    @Override
    public boolean syncEable() {
        return querysyncEnable;
    }

    @Override
    public long maxSyncMills() {
        return maxSyncMills;
    }

}
