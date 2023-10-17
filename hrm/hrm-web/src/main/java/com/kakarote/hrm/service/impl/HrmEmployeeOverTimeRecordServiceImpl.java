package com.kakarote.hrm.service.impl;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.QueryOverTimeRecordPageListBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeOverTimeRecord;
import com.kakarote.hrm.mapper.HrmEmployeeOverTimeRecordMapper;
import com.kakarote.hrm.service.IHrmEmployeeOverTimeRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 员工加班表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-09-07
 */
@Service
public class HrmEmployeeOverTimeRecordServiceImpl extends BaseServiceImpl<HrmEmployeeOverTimeRecordMapper, HrmEmployeeOverTimeRecord> implements IHrmEmployeeOverTimeRecordService {

    @Override
    public BasePage<Map<String, Object>> queryOverTimeRecordPageList(QueryOverTimeRecordPageListBO queryOverTimeRecordPageListBO) {
        BasePage<Map<String, Object>> page = getBaseMapper().queryOverTimeRecordPageList(queryOverTimeRecordPageListBO.parse(), queryOverTimeRecordPageListBO);
        return page;
    }
}
