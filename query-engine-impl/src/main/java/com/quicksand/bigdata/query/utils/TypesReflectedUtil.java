package com.quicksand.bigdata.query.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TypesReflectedUtils
 * <p>
 * com.quicksand.bigdata.query.engine.utils
 *
 * @author xupei
 * @date 2020/12/21
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class TypesReflectedUtil {

    private static final Map<Class<?>, Integer> JAVA_TYPE_TO_SQL_TYPE_MAP = new HashMap<>(32);
    private static final Map<Integer, Class<?>> SQL_TYPE2_JAVA_TYPE_MAP = new HashMap<>(32);

    static {
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(boolean.class, Types.BOOLEAN);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Boolean.class, Types.BOOLEAN);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(byte.class, Types.TINYINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Byte.class, Types.TINYINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(short.class, Types.SMALLINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Short.class, Types.SMALLINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(int.class, Types.INTEGER);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Integer.class, Types.INTEGER);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(long.class, Types.BIGINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Long.class, Types.BIGINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(BigInteger.class, Types.BIGINT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(float.class, Types.FLOAT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Float.class, Types.FLOAT);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(double.class, Types.DOUBLE);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Double.class, Types.DOUBLE);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(BigDecimal.class, Types.DECIMAL);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(java.sql.Date.class, Types.DATE);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(java.sql.Time.class, Types.TIME);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Blob.class, Types.BLOB);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(Clob.class, Types.CLOB);
        JAVA_TYPE_TO_SQL_TYPE_MAP.put(List.class, Types.ARRAY);

        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.BOOLEAN, Boolean.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.TINYINT, Byte.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.SMALLINT, Short.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.INTEGER, Integer.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.BIGINT, Long.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.FLOAT, Float.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.DOUBLE, Double.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.DECIMAL, BigDecimal.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.DATE, Date.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.TIME, Date.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.TIMESTAMP, Date.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.BLOB, Blob.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.CLOB, Clob.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.CHAR, String.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.VARCHAR, String.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.LONGVARCHAR, String.class);
        SQL_TYPE2_JAVA_TYPE_MAP.put(Types.ARRAY, List.class);

    }

    public static Class<?> reflect2Class(int SqlType) {
        return SQL_TYPE2_JAVA_TYPE_MAP.get(SqlType);
    }

}
