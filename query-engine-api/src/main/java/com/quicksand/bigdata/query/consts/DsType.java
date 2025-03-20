package com.quicksand.bigdata.query.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DsType
 *
 * @author xupei
 * @date 2022/8/11
 */
@Getter
@AllArgsConstructor
public enum DsType {

    Mysql(0, "mysql", "mysql"),

    StarRocks(1, "starrocks", "mysql"),

    Doris(2, "doris", "mysql"),

    ;

    @JsonValue
    final int code;

    final String flag;

    /**
     * 协议标识
     */
    final String protocolFlag;

    public static DsType findByFlag(String flag) {
        for (DsType value : DsType.values()) {
            if (value.flag.equalsIgnoreCase(flag)) {
                return value;
            }
        }
        return null;
    }


}
