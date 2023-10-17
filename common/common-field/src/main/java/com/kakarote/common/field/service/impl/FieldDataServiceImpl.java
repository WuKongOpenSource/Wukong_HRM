package com.kakarote.common.field.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.field.entity.FieldData;
import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.ModelField;
import com.kakarote.common.field.mapper.FieldDataMapper;
import com.kakarote.common.field.service.FieldDataService;
import com.kakarote.common.field.utils.FieldUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.utils.BaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * pageElasticsearch
 */
@Service
public class FieldDataServiceImpl implements FieldDataService {


    @Autowired
    private FieldDataMapper fieldDataMapper;


    /**
     * 设置用户数据
     *
     * @param model      模块数据
     * @param fieldLabel 业务类型
     */
    @Override
    public void setDataByBatchId(Map<String, Object> model, FieldLabel fieldLabel) {
        Long dataId = MapUtil.getLong(model, fieldLabel.getPrimaryKey(true));
        List<FieldData> fieldData = fieldDataMapper.queryByDataId(dataId, getTableName(fieldLabel), null);
        for (FieldData data : fieldData) {
            model.put(data.getName(), data.getValue());
        }
    }

    /**
     * 保存自定义字段数据
     *
     * @param array      data
     * @param batchId    batchId
     * @param saveType   1 覆盖 2 跳过 3 更新 4审批节点修改
     * @param dataId     主键ID
     * @param fieldLabel 业务类型
     */
    @Override
    public void saveData(List<? extends ModelField> array, String batchId, Integer saveType, FieldLabel fieldLabel, Long dataId) {
        List<FieldData> fieldDataList = new ArrayList<>();
        if (Objects.equals(3, saveType) || Objects.equals(4, saveType)) {
            List<Long> deleteIds = new ArrayList<>();
            List<FieldData> list = fieldDataMapper.queryByDataId(dataId, getTableName(fieldLabel), null);

            //已存在的数据Id
            List<Long> existIds = list.stream().map(FieldData::getId).collect(Collectors.toList());

            for (FieldData oldData : list) {
                for (ModelField newData : array) {
                    if (oldData.getFieldId().equals(newData.getFieldId())) {
                        if (ObjectUtil.isNotEmpty(newData.getValue())) {
                            deleteIds.add(oldData.getId());
                            existIds.add(newData.getFieldId());
                        }
                    }
                }
            }
            if (!deleteIds.isEmpty()) {
                fieldDataMapper.removeById(deleteIds, getTableName(fieldLabel));
            }
            array.removeIf(obj -> !existIds.contains(obj.getFieldId()) && ObjectUtil.isNotEmpty(obj.getValue()));
        } else {
            deleteByDataId(Collections.singletonList(dataId), fieldLabel);
        }
        LocalDateTime dateTime = LocalDateTimeUtil.now();
        UserInfo user = UserUtil.getUser();
        for (ModelField obj : array) {
            if (ObjectUtil.isNotEmpty(obj.getValue()) && Objects.equals(obj.getType(), FieldEnum.FLOATNUMBER.getType()) && !NumberUtil.isNumber(obj.getValue().toString())) {
                throw new CrmException(2108, String.format("【%s】字段格式不正确！", obj.getName()));
            }
            String value;
            if (obj.getValue() == null) {
                value = "";
            } else {
                value = FieldUtil.format2DbString(obj.getValue(), obj.getType(), obj.getValue().toString());
            }

            FieldData fieldData = new FieldData(obj.getFieldId(), obj.getFieldName(), value, batchId, dataId);
            fieldData.setId(BaseUtil.getNextId());
            fieldData.setCreateTime(dateTime);
            fieldData.setUpdateTime(dateTime);
            fieldData.setCreateUserId(user.getUserId());
            fieldData.setUpdateUserId(user.getUserId());

            fieldDataList.add(fieldData);
        }

        if (fieldDataList.isEmpty()) {
            return;
        }
        fieldDataMapper.saveData(fieldDataList, getTableName(fieldLabel));
    }

    /**
     * 通过dataId删除数据
     *
     * @param dataId     data
     * @param fieldLabel 业务类型
     */
    @Override
    public void deleteByDataId(List<Long> dataId, FieldLabel fieldLabel) {
        if (!dataId.isEmpty()) {
            fieldDataMapper.removeByDataId(dataId, getTableName(fieldLabel));
        }
    }

    /**
     * 保存单个自定义字段数据
     *
     * @param fieldId    字段ID
     * @param dataId     dataId
     * @param fieldLabel 业务类型
     * @param record     业务数据
     * @return data
     */
    @Override
    public String saveData(Long fieldId, String batchId, FieldLabel fieldLabel, JSONObject record, Long dataId) {
        List<FieldData> data = fieldDataMapper.queryByDataId(dataId, getTableName(fieldLabel), Collections.singletonList(fieldId));
        FieldData fieldData = data.isEmpty() ? new FieldData(BaseUtil.getNextId()) : data.get(0);
        String value = fieldData.getValue();
        String newValue = FieldUtil.format2DbString(record.get("value"), record.getInteger("type"), record.getString("value"));
        fieldData.setFieldId(record.getLong("fieldId"));
        fieldData.setName(record.getString("fieldName"));
        fieldData.setValue(newValue);
        fieldData.setCreateTime(LocalDateTimeUtil.now());
        fieldData.setBatchId(batchId);
        fieldData.setDataId(dataId);
        saveData(fieldData, fieldLabel);
        return value;
    }

    /**
     * 查询字段值，主要是附件查询使用
     *
     * @param fieldList  字段列表
     * @param batchId    dataId
     * @param fieldLabel 业务类型
     * @return valueList
     */
    @Override
    public List<String> queryFieldValue(List<? extends ModelField> fieldList, String batchId, FieldLabel fieldLabel) {
        return queryFieldValueByFieldId(fieldList.stream().map(ModelField::getFieldId).collect(Collectors.toList()), batchId, fieldLabel);
    }

    @Override
    public List<String> queryFieldValueByFieldId(List<Long> fieldIds, String dataId, FieldLabel fieldLabel) {
        List<FieldData> fieldData = fieldDataMapper.queryforFileByDataId(dataId, getTableName(fieldLabel), fieldIds);
        return fieldData.stream().map(FieldData::getValue).collect(Collectors.toList());
    }


    /**
     * 保存数据
     *
     * @param fieldData  fieldData
     * @param fieldLabel 业务类型
     */
    @Override
    public void saveData(FieldData fieldData, FieldLabel fieldLabel) {
        LocalDateTime dateTime = LocalDateTime.now();
        UserInfo user = UserUtil.getUser();
        if (fieldData.getId() != null) {
            fieldDataMapper.removeById(Collections.singletonList(fieldData.getId()), getTableName(fieldLabel));
            fieldData.setUpdateTime(dateTime);
            fieldData.setUpdateUserId(user.getUserId());
        } else {
            fieldData.setId(BaseUtil.getNextId());
        }
        fieldData.setCreateTime(dateTime);
        fieldData.setCreateUserId(user.getUserId());
        fieldDataMapper.saveData(Collections.singletonList(fieldData), getTableName(fieldLabel));
    }

    /**
     * 根据字段ID删除数据
     *
     * @param fieldId    字段ID
     * @param fieldLabel 业务类型
     */
    @Override
    public void deleteByFieldId(Long fieldId, FieldLabel fieldLabel) {
        fieldDataMapper.removeByFieldId(fieldId, getTableName(fieldLabel));
    }

    /**
     * 通过数据关联ID查询数据
     *
     * @param dataId     dataId
     * @param fieldLabel 业务类型
     * @return data
     */
    @Override
    public List<FieldData> queryByDataId(Long dataId, FieldLabel fieldLabel) {
        return fieldDataMapper.queryByDataId(dataId, getTableName(fieldLabel), null);
    }

    /**
     * 获取实际的表名，表名为模块名称+表名
     *
     * @param fieldLabel fieldLabel
     * @return tableName
     */
    private String getTableName(FieldLabel fieldLabel) {
        return fieldLabel.getModelName() + "_" + fieldLabel.getTableName();
    }
}
