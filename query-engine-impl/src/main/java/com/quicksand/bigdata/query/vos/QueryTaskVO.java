package com.quicksand.bigdata.query.vos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.consts.TriggerType;
import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * QueryTaskVO
 *
 * @author xupei
 * @date 2022/8/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTaskVO
        implements UniqFlaged, ShareRingBufferEntry<QueryTaskVO> {

    @JsonIgnore
    transient ShareRingBuffer<QueryTaskVO> curBuffer;

    /**
     * 任务id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String id;

    /**
     * 凭据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String voucher;

    /**
     * 触发类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    TriggerType trigger;

    /**
     * 连接信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ConnectionInfoVO connectionInfo;

    /**
     * 查询请求信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    QueryReqInfoVO queryReq;

    /**
     * 任务控制信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ReqControlInfoVO reqControlInfo;

    /* ------------ 响应段 ------------ */

    /**
     * 任务状态
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    JobState state;

    /**
     * 提交时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date submitTime;

    /**
     * 开始时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date startTime;

    /**
     * 完成时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date completeTime;

    /**
     * 结果
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ResultSetVO resultSet;

    @Override
    public String getFlag() {
        return id;
    }

    @Override
    public void persistence() {
        if (null != curBuffer) {
            curBuffer.modifyEntry(this);
        }
    }

    @Override
    public QueryTaskVO reload() {
        QueryTaskVO newInstance = null;
        (null == curBuffer
                ? this
                : (null == (newInstance = curBuffer.fetch(getFlag())) ? this : newInstance)).setCurBuffer(getCurBuffer());
        return newInstance;
    }

}
