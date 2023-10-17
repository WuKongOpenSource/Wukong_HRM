package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeQuotaScore;

import java.util.List;

/**
 * <p>
 * 员工考核计划评分表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
public interface IHrmAppraisalEmployeeQuotaScoreService extends BaseService<HrmAppraisalEmployeeQuotaScore> {

    List<HrmAppraisalEmployeeQuotaScore> queryQuotaScoreList(Long appraisalEmployeeId);
}
