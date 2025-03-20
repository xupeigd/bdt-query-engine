package com.quicksand.bigdata.query.utils;

import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.consts.ResultMode;
import com.quicksand.bigdata.query.vos.ReqControlInfoVO;
import com.quicksand.bigdata.query.vos.ResultSetVO;
import com.quicksand.bigdata.query.vos.SqlColumnMetaDataVO;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ResultSetAnalysis
 * <p>
 * com.quicksand.bigdata.query.engine.ds.impl
 *
 * @author xupei
 * @date 2021/2/3
 */
@Slf4j
public final class ResultSetColumnResolver {

    private ResultSetColumnResolver() {
    }

    /**
     * 分析元数据
     *
     * @param metaData ResultSetMetaData
     * @return List of SqlColumnMetaDataVO
     */
    public static List<SqlColumnMetaDataVO> anlysisMetaData(ResultSetMetaData metaData) throws SQLException {
        List<SqlColumnMetaDataVO> metas = new ArrayList<>();
        int columnCount = metaData.getColumnCount();
        for (int index = 0; index < columnCount; index++) {
            int columnType = metaData.getColumnType(index + 1);
            SqlColumnMetaDataVO sqlColumnMetaDataVO = SqlColumnMetaDataVO.builder()
                    .index(index)
                    .name(metaData.getColumnName(index + 1))
                    .sqlType(columnType)
                    .javaType(TypesReflectedUtil.reflect2Class(columnType))
                    .build();
            metas.add(sqlColumnMetaDataVO);
        }
        return metas;
    }

    @SuppressWarnings("unchecked")
    private static <T> void add2List(List<?> commonList, int index, T t) {
        if (null == t) {
            commonList.add(index, null);
            return;
        }
        List<T> tList = (List<T>) commonList;
        tList.add(index, t);
    }

    @SuppressWarnings("unchecked")
    static <T> T resolveTypeValue(ResultSet sqlRowSet, int columnIndex, int sqlType, Class<T> javaType) throws SQLException {
        Object typeValue = null;
        if (String.class.equals(javaType)) {
            typeValue = sqlRowSet.getString(columnIndex);
        } else if (Integer.class.equals(javaType)) {
            typeValue = sqlRowSet.getInt(columnIndex);
        } else if (Double.class.equals(javaType)) {
            typeValue = sqlRowSet.getDouble(columnIndex);
        } else if (Float.class.equals(javaType)) {
            typeValue = sqlRowSet.getFloat(columnIndex);
        } else if (Long.class.equals(javaType)) {
            typeValue = sqlRowSet.getLong(columnIndex);
        } else if (BigDecimal.class.equals(javaType)) {
            typeValue = sqlRowSet.getBigDecimal(columnIndex);
        } else if (Date.class.equals(javaType)) {
            if (Types.TIME == sqlType) {
                Time time = sqlRowSet.getTime(columnIndex);
                typeValue = null == time ? null : new Date(time.getTime());
            } else if (Types.TIMESTAMP == sqlType) {
                Timestamp timestamp = sqlRowSet.getTimestamp(columnIndex);
                typeValue = null == timestamp ? null : new Date(timestamp.getTime());
            } else if (Types.DATE == sqlType) {
                java.sql.Date date = sqlRowSet.getDate(columnIndex);
                typeValue = null == date ? null : new Date(date.getTime());
            } else {
                typeValue = sqlRowSet.getDate(columnIndex);
            }
        } else if (Short.class.equals(javaType)) {
            typeValue = sqlRowSet.getShort(columnIndex);
        } else if (Byte.class.equals(javaType)) {
            typeValue = sqlRowSet.getByte(columnIndex);
        } else if (Boolean.class.equals(javaType)) {
            typeValue = sqlRowSet.getBoolean(columnIndex);
        } else if (List.class.equals(javaType)) {
            Object arrayObj = sqlRowSet.getObject(columnIndex);
            if (null == arrayObj) {
                typeValue = new ArrayList<>();
            } else {
                //直接粗暴
                String columnValue = JsonUtils.toJsonString(arrayObj);
                log.warn("resolveTypeValue cv:{}`", columnValue);
                List valueList = JsonUtils.parseTo(columnValue, List.class);
                typeValue = null == valueList ? new ArrayList<>() : valueList;
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + javaType);
        }
        return null == typeValue ? null : (T) typeValue;
    }

    private static void anlysisColumnValues(List<SqlColumnMetaDataVO> metas, List<List<?>> resultValue, ResultSet sqlRowSet, int fetchSize, int curIndex) throws SQLException {
        for (int columnIndex = 0; columnIndex < metas.size(); columnIndex++) {
            SqlColumnMetaDataVO columnMeta = metas.get(columnIndex);
            Class<?> javaType = columnMeta.getJavaType();
            int sqlType = columnMeta.getSqlType();
            List<?> acceptList = resultValue.get(columnIndex);
            add2List(acceptList, curIndex, resolveTypeValue(sqlRowSet, columnIndex + 1, sqlType, javaType));
        }
    }

    @SuppressWarnings("unchecked")
    private static void anlysisRowValues(List<SqlColumnMetaDataVO> metas, List<List<?>> resultValues, ResultSet resultSet, int fetchSize, int curIndex) throws SQLException {
        List<Object> vList = new ArrayList<>();
        for (int i = 0; i < metas.size(); i++) {
            SqlColumnMetaDataVO columnMeta = metas.get(i);
            vList.add(resolveTypeValue(resultSet, i + 1, columnMeta.getSqlType(), columnMeta.getJavaType()));
        }
        resultValues.add(vList);
    }

    public static ResultSetVO resolve(ResultSet resultSet, ReqControlInfoVO reqControlInfo) {
        ResultSetVO resultSetVO = ResultSetVO.builder()
                .state(JobState.Executing)
                .msg("resolve result . ")
                .build();
        Try.run(() -> {
                    int fetchSize = resultSet.getFetchSize();
                    List<SqlColumnMetaDataVO> vos = anlysisMetaData(resultSet.getMetaData());
                    int curIndex = 0;
                    int row = resultSet.getRow();
                    List<List<?>> resultValues;
                    if (Objects.equals(ResultMode.Column, reqControlInfo.getResultMode())) {
                        //列式输出
                        resultValues = initResultValues(vos, fetchSize);
                        while (curIndex < row || resultSet.next()) {
                            anlysisColumnValues(vos, resultValues, resultSet, fetchSize, curIndex);
                            curIndex++;
                        }
                        resultSetVO.setColumns(resultValues);
                    } else {
                        resultValues = new ArrayList<>(fetchSize);
                        while (curIndex < row || resultSet.next()) {
                            anlysisRowValues(vos, resultValues, resultSet, fetchSize, curIndex);
                            curIndex++;
                        }
                        resultSetVO.setRows(resultValues);
                    }
                    resultSetVO.setMsg("query complete !");
                    resultSetVO.setColumnMetas(vos);
                    resultSetVO.setState(JobState.Success);
                })
                .onFailure(ex -> {
                    resultSetVO.setState(JobState.Fail);
                    resultSetVO.setMsg("query fail ! " + (null == ex.getMessage() ? "" : ex.getMessage()));
                });
        return resultSetVO;
    }

    private static List<List<?>> initResultValues(List<SqlColumnMetaDataVO> vos, int fetchSize) {
        List<List<?>> resultValues = new ArrayList<>(vos.size());
        for (SqlColumnMetaDataVO metaDataVO : vos) {
            Class<?> javaType = metaDataVO.getJavaType();
            List<?> objectList;
            if (String.class.equals(javaType)) {
                objectList = new ArrayList<String>();
            } else if (Integer.class.equals(javaType)) {
                objectList = new ArrayList<Integer>();
            } else if (Double.class.equals(javaType)) {
                objectList = new ArrayList<Double>();
            } else if (Float.class.equals(javaType)) {
                objectList = new ArrayList<Float>();
            } else if (Long.class.equals(javaType)) {
                objectList = new ArrayList<Long>();
            } else if (BigDecimal.class.equals(javaType)) {
                objectList = new ArrayList<BigDecimal>();
            } else if (Date.class.equals(javaType)) {
                objectList = new ArrayList<Date>();
            } else if (Short.class.equals(javaType)) {
                objectList = new ArrayList<Short>();
            } else if (Byte.class.equals(javaType)) {
                objectList = new ArrayList<Byte>();
            } else if (Boolean.class.equals(javaType)) {
                objectList = new ArrayList<Boolean>();
            } else if (List.class.equals(javaType)) {
                objectList = new ArrayList<List<Object>>();
            } else {
                throw new IllegalStateException("Unexpected value: " + javaType);
            }
            resultValues.add(objectList);
        }
        return resultValues;
    }

}
