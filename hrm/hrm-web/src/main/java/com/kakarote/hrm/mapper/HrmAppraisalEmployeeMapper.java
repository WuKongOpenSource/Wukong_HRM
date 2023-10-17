package com.kakarote.hrm.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryEmployeePendingAppraisalBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeQuotaBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployee;
import com.kakarote.hrm.entity.VO.EmployeeAppraisalPlanVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工绩效考核-考核绩效基本信息 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
public interface HrmAppraisalEmployeeMapper extends BaseMapper<HrmAppraisalEmployee> {

    EmployeeAppraisalPlanVO queryBaseInfo(@Param("employeeAppraisalId") Long employeeAppraisalId);

    BasePage<EmployeeAppraisalPlanVO> queryAppraisalList(BasePage<EmployeeAppraisalPlanVO> parse, @Param("data") QueryEmployeeQuotaBO queryEmployeeQuotaBO);

    BasePage<EmployeeAppraisalPlanVO> queryStageAppraisalList(BasePage<EmployeeAppraisalPlanVO> parse, @Param("data") QueryEmployeePendingAppraisalBO queryEmployeePendingAppraisalBO);

    Map<String, Object> queryStageEmployeeNum(@Param("appraisalPlanId") Long appraisalPlanId);

    @InterceptorIgnore(tenantLine = "true")
    List<HrmAppraisalEmployee> queryEmployeeListByPlanId(@Param("appraisalPlanId") Long appraisalPlanId);

    @InterceptorIgnore(tenantLine = "true")
    HrmAppraisalEmployee queryRecentlyAppraisalEmployee(@Param("employeeId") Long employeeId);
}
