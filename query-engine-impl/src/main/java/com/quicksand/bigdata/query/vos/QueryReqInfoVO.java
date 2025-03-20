package com.quicksand.bigdata.query.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeMap;

/**
 * QueryReqInfoVO
 *
 * @author xupei
 * @date 2022/8/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryReqInfoVO {

    /**
     * SQL的Md5
     * (不参与标识计算)
     */
    String sqlMd5;

    /**
     * 参数的Md5
     * (不参与标识计算)
     */
    String paramsMd5;

    /**
     * 模板sql
     */
    String templateSql;

    /**
     * 参数
     */
    TreeMap<String, Object> paramters;


}
