package com.kakarote.common.log.service.impl;

import com.kakarote.common.log.entity.OperateLogEntity;
import com.kakarote.common.log.service.OperateLogSaveService;
import org.springframework.stereotype.Service;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/24
 */
@Service
public class MQSaveServiceImpl implements OperateLogSaveService {

    @Override
    public void saveOperateLog(OperateLogEntity.Entity.Builder entity) {

    }
}
