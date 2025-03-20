package com.quicksand.bigdata.query.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SqlColumnMetaModel {

    int index;

    String name;

    int sqlType;

    @JsonIgnore
    Class<?> javaType;

}
