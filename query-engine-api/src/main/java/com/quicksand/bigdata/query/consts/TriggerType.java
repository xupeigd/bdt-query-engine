package com.quicksand.bigdata.query.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TriggerType
 *
 * @author xupei
 * @date 2022/8/10
 */
@Getter
@AllArgsConstructor
public enum TriggerType {

    /**
     * 即席查询
     */
    AdHoc(0, "Ad-Hoc"),

    /**
     * 调度
     */
    Schedule(1, "Schedule"),

    /**
     * 启发
     */
    Heuristic(2, "Heuristic"),

    /**
     * 预测
     */
    Prophecy(3, "Prophecy"),
    ;

    @JsonValue
    final int value;

    final String flag;

}
