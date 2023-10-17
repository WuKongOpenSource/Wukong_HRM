package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AppraisalPlanQuotaSettingBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanQuotaSetting;
import com.kakarote.hrm.entity.VO.AppraisalPlanQuotaSettingVO;

/**
 * <p>
 * 考核计划-指标配置-基础表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
public interface IHrmAppraisalPlanQuotaSettingService extends BaseService<HrmAppraisalPlanQuotaSetting> {

    void saveQuotaSetting(AppraisalPlanQuotaSettingBO appraisalPlanQuotaSettingBO, Long appraisalPlan);

    /**
     * 查看考核计划指标设置
     *
     * @param appraisalPlan
     * @return
     */
    AppraisalPlanQuotaSettingVO queryPlanQuotaSettingVO(Long appraisalPlan);


    //删除指标设置
    void deleteByAppraisalPlanId(Long appraisalPlanId);
}
