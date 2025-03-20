package com.quicksand.bigdata.query.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * QueryModel
 *
 * @author xupei
 * @date 2022/8/8
 */
@Getter
@AllArgsConstructor
public enum QueryMode {

    /**
     * 异步
     */
    Async(0),

    /**
     * 同步
     */
    Sync(1),

    ;

    @JsonValue
    final int code;

}
