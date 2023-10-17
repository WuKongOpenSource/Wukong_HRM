package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AppraisalPlanProcessSettingBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanProcessSetting;
import com.kakarote.hrm.entity.VO.AppraisalPlanProcessSettingVO;

/**
 * <p>
 * 考核计划-流程配置-基础表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
public interface IHrmAppraisalPlanProcessSettingService extends BaseService<HrmAppraisalPlanProcessSetting> {

    //保存考核计划流程配置
    void saveAppraisalPlanProcessSetting(AppraisalPlanProcessSettingBO appraisalPlanProcessSettingBO, Long appraisalPlan);

    AppraisalPlanProcessSettingVO queryProcessSettingVO(Long appraisalPlan);

    //删除流程设置总体
    void delProcessSetting(Long processId);
}
