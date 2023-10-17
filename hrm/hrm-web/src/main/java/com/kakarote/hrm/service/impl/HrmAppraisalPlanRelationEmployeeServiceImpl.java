package com.kakarote.hrm.service.impl;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.QueryAppraisalEmployeeBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanRelationEmployee;
import com.kakarote.hrm.entity.VO.AppraisalEmployeeVO;
import com.kakarote.hrm.mapper.HrmAppraisalPlanRelationEmployeeMapper;
import com.kakarote.hrm.service.IHrmAppraisalPlanRelationEmployeeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 绩效考核关联员工表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-23
 */
@Service
public class HrmAppraisalPlanRelationEmployeeServiceImpl extends BaseServiceImpl<HrmAppraisalPlanRelationEmployeeMapper, HrmAppraisalPlanRelationEmployee> implements IHrmAppraisalPlanRelationEmployeeService {

    @Override
    public BasePage<AppraisalEmployeeVO> queryAppraisalEmployeeList(QueryAppraisalEmployeeBO queryAppraisalEmployeeBO) {
        return baseMapper.queryEmployeeList(queryAppraisalEmployeeBO.parse(), queryAppraisalEmployeeBO);
    }
}
