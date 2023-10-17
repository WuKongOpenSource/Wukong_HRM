package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryOverTimeRecordPageListBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeOverTimeRecord;

import java.util.Map;

/**
 * <p>
 * 员工加班表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-09-07
 */
public interface IHrmEmployeeOverTimeRecordService extends BaseService<HrmEmployeeOverTimeRecord> {
    /**
     * 查询加班列表
     *
     * @param queryOverTimeRecordPageListBO
     * @return
     */
    BasePage<Map<String, Object>> queryOverTimeRecordPageList(QueryOverTimeRecordPageListBO queryOverTimeRecordPageListBO);
}
