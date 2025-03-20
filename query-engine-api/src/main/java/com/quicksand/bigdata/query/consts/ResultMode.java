package com.quicksand.bigdata.query.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ResultMode
 * (结果模式)
 *
 * @author xupei
 * @date 2022/8/11
 */
@Getter
@AllArgsConstructor
public enum ResultMode {

    /**
     * 列输出
     */
    Column(0),

    /**
     * 行输出
     */
    Row(1),

    ;

    @JsonValue
    final int code;

}
