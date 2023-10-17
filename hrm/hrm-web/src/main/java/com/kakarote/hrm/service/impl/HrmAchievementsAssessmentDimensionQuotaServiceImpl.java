package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimensionQuota;
import com.kakarote.hrm.mapper.HrmAchievementsAssessmentDimensionQuotaMapper;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentDimensionQuotaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 考核指标 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Service
public class HrmAchievementsAssessmentDimensionQuotaServiceImpl extends BaseServiceImpl<HrmAchievementsAssessmentDimensionQuotaMapper, HrmAchievementsAssessmentDimensionQuota> implements IHrmAchievementsAssessmentDimensionQuotaService {

    @Override
    public List<HrmAchievementsAssessmentDimensionQuota> queryQuotaListByDimensionIds(List<Long> dimensionIds) {
        List<HrmAchievementsAssessmentDimensionQuota> quotaList = lambdaQuery().select().in(HrmAchievementsAssessmentDimensionQuota::getDimensionId, dimensionIds).list();
        return quotaList;
    }
}
