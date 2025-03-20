package com.quicksand.bigdata.query.services;

/**
 * HotLoadingConfig
 * （热加载配置）
 *
 * @author xupei
 * @date 2022/8/10
 */
public interface HotLoadingConfig {

    /**
     * 是否支持sync模式
     *
     * @return true/false
     */
    boolean syncEable();

    /**
     * sync模式最大等待mills
     *
     * @return long
     */
    long maxSyncMills();

}
