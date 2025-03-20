package com.quicksand.bigdata.query.vos;

import com.quicksand.bigdata.query.consts.QueryMode;
import com.quicksand.bigdata.query.consts.ResultMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TaskControlInfoVO
 * (任务控制信息)
 *
 * @author xupei
 * @date 2022/8/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqControlInfoVO {

    /**
     * 查询模式
     * <p>
     * 0  异步 1 同步
     * (不参与标识计算)
     */
    QueryMode mode;

    /**
     * 结果输出模式
     * <p>
     * 0 列 1 行
     */
    ResultMode resultMode;

    /**
     * 同步模式超时时长
     * （ms）
     * (不参与标识计算)
     */
    Long syncMills;

    /**
     * 异步模式超时时长
     * （ms）
     * (不参与标识计算)
     */
    Long asyncMills;

}
