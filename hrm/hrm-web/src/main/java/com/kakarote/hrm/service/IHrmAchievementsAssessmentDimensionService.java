package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimension;

import java.util.List;

/**
 * <p>
 * 考核维度 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface IHrmAchievementsAssessmentDimensionService extends BaseService<HrmAchievementsAssessmentDimension> {

    List<HrmAchievementsAssessmentDimension> queryDimensionListByTemplateId(Long templateId);
}
