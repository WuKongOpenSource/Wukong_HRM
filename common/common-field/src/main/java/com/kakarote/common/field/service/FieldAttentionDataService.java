package com.kakarote.common.field.service;

import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.ModelField;

import java.util.Map;

/**
 * <p>
 * 关注度字段存值表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-10-23
 */
public interface FieldAttentionDataService {

    /**
     * 生成关注度数据
     *
     * @param dataMap 字段值列表
     * @param filed   字段信息
     */
    void saveOrUpdate(FieldLabel fieldLabel, ModelField filed, Map<String, Object> dataMap, Object id);

}
