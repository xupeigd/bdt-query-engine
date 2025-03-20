package com.quicksand.bigdata.query.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.consts.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * QueryRespModel
 *
 * @author xupei
 * @date 2022/8/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRespModel {

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
     * 创建参数
     */
    QueryReqModel req;

    /**
     * 提交时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "GMT+8")
    Date submitTime;

    /**
     * 开始时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "GMT+8")
    Date startTime;

    /**
     * 结束时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "GMT+8")
    Date completeTime;

    /**
     * 任务状态
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    JobState state;

    /**
     * 结果集
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ResultSetModel resultSet;


}
