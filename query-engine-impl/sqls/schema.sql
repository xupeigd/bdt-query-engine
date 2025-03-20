SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_query_ids
-- ----------------------------

CREATE TABLE `t_query_ids`
(
    `sequence_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '序列名称',
    `next_val`      bigint DEFAULT NULL COMMENT '下一值',
    PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '序号记录表';

-- ----------------------------
-- Table structure for t_query_connection_infos
-- ----------------------------

CREATE TABLE `t_query_connection_infos`
(
    `id`               bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键',
    `address`          varchar(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '连接地址',
    `comment`          varchar(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`      datetime                         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `default_database` varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '默认数据库',
    `default_schema`   varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '默认数据库',
    `flag`             varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '连接信息的识别标识，唯一',
    `name`             varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '名称，唯一',
    `password`         varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '密码',
    `status`           tinyint                          NOT NULL DEFAULT '0' COMMENT '数据状态 0 删除 1 可用',
    `type`             tinyint                          NOT NULL DEFAULT '0' COMMENT '连接信息类型 0 mysql 1 starrocks 2 doris',
    `update_time`      datetime                         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `user_name`        varchar(32) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '用户',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_flag` (`flag`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '连接信息实体表';

SET
FOREIGN_KEY_CHECKS = 1;