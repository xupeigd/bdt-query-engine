package com.quicksand.bigdata.query.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SqlColumnMetaData
 * <p>
 * com.quicksand.bigdata.query.engine.vos
 *
 * @author xupei
 * @date 2020/12/21
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SqlColumnMetaDataVO {

    int index;

    String name;

    int sqlType;

    Class<?> javaType;

}
