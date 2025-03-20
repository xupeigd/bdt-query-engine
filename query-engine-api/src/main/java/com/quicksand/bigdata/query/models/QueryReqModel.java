package com.quicksand.bigdata.query.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.query.consts.QueryMode;
import com.quicksand.bigdata.query.consts.ResultMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.TreeMap;

/**
 * QueryReqModel
 *
 * @author xupei
 * @date 2022/8/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryReqModel {

    /**
     * 连接信息
     */
    @NotNull
    ConnectionInfoModel connectionInfo;

    /**
     * 模板sql
     */
    String templateSql;

    /**
     * 参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    TreeMap<String, Object> paramters;

    /**
     * 查询模式
     * <p>
     * 0  异步 1 同步
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
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long syncMills;

    /**
     * 异步模式超时时长
     * （ms）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long asyncMills;


}
