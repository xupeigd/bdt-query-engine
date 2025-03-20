package com.quicksand.bigdata.query.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.query.consts.DsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConnectionInfo
 * (连接信息)
 *
 * @author xupei
 * @date 2022/8/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionInfoModel {

    /**
     * 连接信息标识
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String flag;

    /**
     * 连接名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 类型
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DsType type;

    /**
     * 地址
     * <p>
     * host:port
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String address;

    /**
     * 默认的数据库
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String defaultDatabase;

    /**
     * 默认schema
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String defaultSchema;

    /**
     * 连接用户明
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String userName;

    /**
     * 连接密码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String password;

    /**
     * 备注信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String comment;

}
