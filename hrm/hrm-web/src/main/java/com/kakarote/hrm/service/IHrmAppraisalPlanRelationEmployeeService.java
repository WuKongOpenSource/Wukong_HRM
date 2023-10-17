package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryAppraisalEmployeeBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanRelationEmployee;
import com.kakarote.hrm.entity.VO.AppraisalEmployeeVO;

/**
 * <p>
 * 绩效考核关联员工表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-23
 */
public interface IHrmAppraisalPlanRelationEmployeeService extends BaseService<HrmAppraisalPlanRelationEmployee> {

    BasePage<AppraisalEmployeeVO> queryAppraisalEmployeeList(QueryAppraisalEmployeeBO queryAppraisalEmployeeBO);
}
