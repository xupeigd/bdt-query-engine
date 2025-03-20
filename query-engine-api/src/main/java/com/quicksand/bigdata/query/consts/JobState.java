package com.quicksand.bigdata.query.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JobState
 *
 * @author xupei
 * @date 2022/8/8
 */
@Getter
@AllArgsConstructor
public enum JobState {

    /**
     * 就绪
     */
    Ready(0),

    /**
     * 执行中
     */
    Executing(1),

    /**
     * 取消
     */
    Cancel(2),

    /**
     * 成功
     */
    Success(3),

    /**
     * 失败
     */
    Fail(4),

    ;

    @JsonValue
    final int code;


}
