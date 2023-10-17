package com.kakarote.common.field.mapper;

import com.kakarote.common.field.entity.FieldData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 自定义字段保存mapper
 * </p>
 *
 * @author zhangzhiwei
 * @since 2023-07-11
 */
@Mapper
public interface FieldDataMapper {


    /**
     * 通过数据关联ID查询数据
     *
     * @param dataId      dataId
     * @param tableName   表名
     * @param fieldIdList 字段列表
     * @return data
     */
    List<FieldData> queryByDataId(@Param("dataId") Long dataId, @Param("tableName") String tableName, @Param("fieldIdList") List<Long> fieldIdList);

    /**
     * 通过数据关联ID查询数据(文件查询使用)
     *
     * @param batchId      batchId
     * @param tableName   表名
     * @param fieldIdList 字段列表
     * @return data
     */
    public List<FieldData> queryforFileByDataId(@Param("batchId") String batchId, @Param("tableName") String tableName, @Param("fieldIdList") List<Long> fieldIdList);

    /**
     * 通过数据关联ID删除数据
     *
     * @param dataIdList dataId列表
     * @param tableName  表名
     */
    void removeByDataId(@Param("dataIdList") List<Long> dataIdList, @Param("tableName") String tableName);

    /**
     * 通过ID删除数据
     *
     * @param idList    id列表
     * @param tableName 表名
     */
    void removeById(@Param("idList") List<Long> idList, @Param("tableName") String tableName);

    /**
     * 通过字段ID删除数据
     *
     * @param fieldId   字段ID
     * @param tableName 表名
     */
    void removeByFieldId(@Param("fieldId") Long fieldId, @Param("tableName") String tableName);

    /**
     * 保存数据
     *
     * @param dataList  数据列表
     * @param tableName 表名
     */
    void saveData(@Param("dataList") List<FieldData> dataList, @Param("tableName") String tableName);
}
