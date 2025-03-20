package com.quicksand.bigdata.query.dbvos;

import com.quicksand.bigdata.query.consts.DataStatus;
import com.quicksand.bigdata.query.consts.DsType;
import com.quicksand.bigdata.query.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * ConnectionInfoDBVO
 *
 * @author xupei
 * @date 2022/8/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_CONNECTION_INFOS,
        indexes = {
                @Index(name = "uniq_flag", columnList = "flag", unique = true),
                @Index(name = "uniq_name", columnList = "name"),
        })
@Where(clause = " status = 1 ")
public class ConnectionInfoDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 连接信息标识
     */
    @Column(columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '连接信息的识别标识，唯一' ")
    String flag;

    /**
     * 连接名称
     */
    @Column(columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '名称，唯一' ")
    String name;

    /**
     * 类型
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '连接信息类型 0 mysql 1 starrocks 2 doris' ")
    DsType type;

    /**
     * 地址
     * <p>
     * host:port
     */
    @Column(columnDefinition = " VARCHAR(255) NOT NULL DEFAULT '' COMMENT '连接地址' ")
    String address;

    /**
     * 默认的数据库
     */
    @Column(name = "default_database", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '默认数据库' ")
    String defaultDatabase;

    /**
     * 默认schema
     */
    @Column(name = "default_schema", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '默认数据库' ")
    String defaultSchema;

    /**
     * 连接用户明
     */
    @Column(name = "user_name", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '用户' ")
    String userName;

    /**
     * 连接密码
     */
    @Column(name = "password", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '密码' ")
    String password;

    /**
     * 备注信息
     */
    @Column(columnDefinition = " VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注' ")
    String comment;

    @Column(name = "create_time", columnDefinition = " datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    @Column(name = "update_time", columnDefinition = " datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 数据状态（逻辑删除）
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '数据状态 0 删除 1 可用' ")
    DataStatus status;


}
