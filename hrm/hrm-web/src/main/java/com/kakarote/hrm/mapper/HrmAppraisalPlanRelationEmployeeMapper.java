package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryAppraisalEmployeeBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanRelationEmployee;
import com.kakarote.hrm.entity.VO.AppraisalEmployeeVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 绩效考核关联员工表 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-23
 */
public interface HrmAppraisalPlanRelationEmployeeMapper extends BaseMapper<HrmAppraisalPlanRelationEmployee> {


    /**
     * 查询待考核员工列表
     *
     * @param queryAppraisalEmployeeBO
     * @return
     */
    BasePage<AppraisalEmployeeVO> queryEmployeeList(BasePage<AppraisalEmployeeVO> page, @Param("data") QueryAppraisalEmployeeBO queryAppraisalEmployeeBO);
}
