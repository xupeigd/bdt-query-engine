package com.quicksand.bigdata.query.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.query.consts.JobState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ResultSetVO
 * <p>
 * com.quicksand.bigdata.query.engine.vos
 *
 * @author xupei
 * @date 2020/12/21
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResultSetModel {

    /**
     * 任务状态
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    JobState state;

    /**
     * 执行提示
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String msg;

    /**
     * metaData
     * key String(Name of Column)
     * value Class of column
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SqlColumnMetaModel> columnMetas;

    /**
     * 数据set
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<List<?>> rows;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<List<?>> columns;

}
