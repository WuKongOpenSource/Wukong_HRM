package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimension;
import com.kakarote.hrm.mapper.HrmAchievementsAssessmentDimensionMapper;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentDimensionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 考核维度 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Service
public class HrmAchievementsAssessmentDimensionServiceImpl extends BaseServiceImpl<HrmAchievementsAssessmentDimensionMapper, HrmAchievementsAssessmentDimension> implements IHrmAchievementsAssessmentDimensionService {

    @Override
    public List<HrmAchievementsAssessmentDimension> queryDimensionListByTemplateId(Long templateId) {
        List<HrmAchievementsAssessmentDimension> dimensionList = lambdaQuery().select().eq(HrmAchievementsAssessmentDimension::getTemplateId, templateId).list();
        return dimensionList;
    }
}
