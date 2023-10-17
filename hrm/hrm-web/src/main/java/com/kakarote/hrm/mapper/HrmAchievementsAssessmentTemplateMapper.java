package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryAchievementsTemplateBO;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentTemplate;
import com.kakarote.hrm.entity.VO.AchievementsTemplatePageVO;
import com.kakarote.hrm.entity.VO.AchievementsTemplateVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 绩效管理-考核模板 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface HrmAchievementsAssessmentTemplateMapper extends BaseMapper<HrmAchievementsAssessmentTemplate> {

    BasePage<AchievementsTemplatePageVO> queryTemplateList(BasePage<QueryAchievementsTemplateBO> parse, @Param("templateName") String templateName);


    AchievementsTemplateVO queryTemplateBaseInformation(@Param("templateId") Long templateId);
}
