package com.kakarote.common.log.service;

import com.kakarote.common.log.entity.OperateLogEntity;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/24
 */
public interface OperateLogSaveService {

    void saveOperateLog(OperateLogEntity.Entity.Builder entity);
}
