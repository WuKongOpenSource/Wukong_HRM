package com.kakarote.core.feign.jxc.service.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.crm.entity.ExamineField;
import com.kakarote.core.feign.examine.entity.ExamineFlowFieldUpdateLogBO;
import com.kakarote.core.feign.examine.entity.ExamineUpdateFieldBO;
import com.kakarote.core.feign.jxc.entity.JxcExamine;
import com.kakarote.core.feign.jxc.entity.JxcState;
import com.kakarote.core.feign.jxc.service.JxcExamineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wwl
 * @date 2022/7/13 16:00
 */
@Component
public class JxcExamineServiceImpl implements JxcExamineService {


    @Override
    public Result examine(Integer label, Integer state, Long id) {
        return null;
    }

    @Override
    public Result<Map<String, Object>> examineFieldDataMap(Integer label, Long id) {
        return null;
    }

    @Override
    public Result examineMessage(JxcExamine jxcExamine) {
        return null;
    }

    @Override
    public Result<JxcState> queryJxcById(Integer label, Long id) {
        return null;
    }

    @Override
    public Result startJxc(Long userId, Long companyId) {
        return null;
    }

    @Override
    public Result<Boolean> initJxcData() {
        return null;
    }

    @Override
    public Result<List<ExamineField>> queryExamineField(Integer label) {
        return null;
    }


    @Override
    public void updateFieldInfo(ExamineUpdateFieldBO examineUpdateFieldBO) {

    }

    @Override
    public Result<List<Map<String, Object>>> queryFieldInfo(Integer label, Long typeId) {
        return null;
    }

    @Override
    public Result<List<Map<String, Object>>> queryExamineUpdateLog(List<ExamineFlowFieldUpdateLogBO> list) {
        return null;
    }

}
