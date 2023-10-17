package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryAchievementsResultTemplateBO;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplate;
import com.kakarote.hrm.entity.VO.ResultTemplatePageVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface HrmAchievementsResultTemplateMapper extends BaseMapper<HrmAchievementsResultTemplate> {

    BasePage<ResultTemplatePageVO> queryResultTemplateList(BasePage<QueryAchievementsResultTemplateBO> parse, @Param("resultTemplateName") String resultTemplateName);
}
