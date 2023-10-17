package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanInspectionScope;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
public interface IHrmAppraisalPlanInspectionScopeService extends BaseService<HrmAppraisalPlanInspectionScope> {

    Map<Long, String> queryInspectionScope(List<Long> appraisalPlanIdList);
}
