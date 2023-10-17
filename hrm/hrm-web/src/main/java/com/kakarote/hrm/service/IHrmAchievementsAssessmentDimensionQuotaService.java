package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimensionQuota;

import java.util.List;

/**
 * <p>
 * 考核指标 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface IHrmAchievementsAssessmentDimensionQuotaService extends BaseService<HrmAchievementsAssessmentDimensionQuota> {

    /**
     * 通过维度id列表查询指标列表
     *
     * @param dimensionIds
     * @return
     */
    List<HrmAchievementsAssessmentDimensionQuota> queryQuotaListByDimensionIds(List<Long> dimensionIds);
}
