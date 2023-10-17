package com.kakarote.common.field.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.field.entity.FieldData;
import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.ModelField;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiwei
 * pageElasticsearch
 */
public interface FieldDataService {

    /**
     * 设置用户数据
     *
     * @param crmModel crmModel
     * @param crmEnum  crm业务类型
     */
    void setDataByBatchId(Map<String, Object> crmModel, FieldLabel crmEnum);

    /**
     * 保存自定义字段数据
     *
     * @param array    data
     * @param batchId  batchId
     * @param saveType 1 覆盖 2 跳过 3 更新
     * @param crmEnum  crm业务类型
     */
    void saveData(List<? extends ModelField> array, String batchId, Integer saveType, FieldLabel crmEnum, Long dataId);

    /**
     * 通过batchId删除数据
     *
     * @param dataId  data
     * @param crmEnum crm业务类型
     */
    void deleteByDataId(List<Long> dataId, FieldLabel crmEnum);

    /**
     * 保存单个自定义字段数据
     *
     * @param fieldId 字段ID
     * @param dataId  dataId
     * @param crmEnum crm业务类型
     * @param record  业务数据
     * @param batchId
     * @return data
     */
    String saveData(Long fieldId, String batchId, FieldLabel crmEnum, JSONObject record, Long dataId);

    /**
     * 查询字段值，主要是附件查询使用
     *
     * @param fieldList 字段列表
     * @param batchId   batchId
     * @param crmEnum   crm业务类型
     * @return valueList
     */
    List<String> queryFieldValue(List<? extends ModelField> fieldList, String batchId, FieldLabel crmEnum);

    List<String> queryFieldValueByFieldId(List<Long> fieldIds, String dataId, FieldLabel crmEnum);

    /**
     * 保存数据
     *
     * @param fieldData fieldData
     * @param crmEnum   crm业务类型
     */
    void saveData(FieldData fieldData, FieldLabel crmEnum);

    /**
     * 根据字段ID删除数据
     *
     * @param fieldId 字段ID
     * @param crmEnum crm业务类型
     */
    void deleteByFieldId(Long fieldId, FieldLabel crmEnum);


    /**
     * 通过数据关联ID查询数据
     *
     * @param dataId     dataId
     * @param fieldLabel 业务类型
     * @return data
     */
    List<FieldData> queryByDataId(Long dataId, FieldLabel fieldLabel);
}
