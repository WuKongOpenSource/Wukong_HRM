package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryAppraisalPlanBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeQuotaBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlan;
import com.kakarote.hrm.entity.VO.AppraisalPlanOfEmployeeVO;
import com.kakarote.hrm.entity.VO.AppraisalPlanVO;
import com.kakarote.hrm.entity.VO.EmployeeAppraisalPlanVO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * 考核计划基础信息表 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
public interface HrmAppraisalPlanMapper extends BaseMapper<HrmAppraisalPlan> {

    BasePage<AppraisalPlanOfEmployeeVO> queryAppraisalOfEmployeeList(BasePage<EmployeeAppraisalPlanVO> parse, @Param("data") QueryEmployeeQuotaBO queryEmployeeQuotaBO);

    BasePage<AppraisalPlanVO> queryAppraisalPlanPageListByStatus(@Param("parse") BasePage<QueryAppraisalPlanBO> parse, @Param("appraisalPlanName") String appraisalPlanName, @Param("status") Integer status, @Param("employeeIds") Collection<Long> employeeIds);
}
