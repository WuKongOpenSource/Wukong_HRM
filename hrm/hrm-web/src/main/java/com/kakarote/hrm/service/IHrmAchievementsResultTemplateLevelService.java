package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplateLevel;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface IHrmAchievementsResultTemplateLevelService extends BaseService<HrmAchievementsResultTemplateLevel> {

    /**
     * 根据模板id查询等级列表
     *
     * @param templateId
     * @return
     */
    List<HrmAchievementsResultTemplateLevel> queryListByTemplateId(Long templateId);
}
