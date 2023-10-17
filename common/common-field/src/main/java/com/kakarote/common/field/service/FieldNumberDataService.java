package com.kakarote.common.field.service;

import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.ModelField;

import java.util.Map;

/**
 * <p>
 * 自定义编号字段存值表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-08-26
 */
public interface FieldNumberDataService {

    /**
     * 生成唯一编号字段
     *
     * @param dataMap 字段值列表
     * @param filed   字段信息
     * @return number
     */
    String generateNumber(FieldLabel fieldLabel, ModelField filed, Map<String, Object> dataMap);
}
