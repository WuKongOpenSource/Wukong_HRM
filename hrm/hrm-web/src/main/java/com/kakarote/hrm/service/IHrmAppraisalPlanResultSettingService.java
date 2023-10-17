package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AppraisalPlanResultSettingBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanResultSetting;
import com.kakarote.hrm.entity.VO.AppraisalPlanResultSettingVO;

import java.util.List;

/**
 * <p>
 * 绩效考核结果等级列表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
public interface IHrmAppraisalPlanResultSettingService extends BaseService<HrmAppraisalPlanResultSetting> {

    /**
     * 保存结果设置
     *
     * @param appraisalPlanResultSettingBOList
     * @param appraisalPlanId
     */
    void savePlanResultSetting(List<AppraisalPlanResultSettingBO> appraisalPlanResultSettingBOList, Long appraisalPlanId);

    /**
     * 根据计划id查询结果设置列表
     *
     * @param appraisalPlanId
     * @return
     */
    List<AppraisalPlanResultSettingVO> queryResultSettingList(Long appraisalPlanId);


}
