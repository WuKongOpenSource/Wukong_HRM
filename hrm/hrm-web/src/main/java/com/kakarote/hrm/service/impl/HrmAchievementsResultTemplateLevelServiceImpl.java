package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplateLevel;
import com.kakarote.hrm.mapper.HrmAchievementsResultTemplateLevelMapper;
import com.kakarote.hrm.service.IHrmAchievementsResultTemplateLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Service
public class HrmAchievementsResultTemplateLevelServiceImpl extends BaseServiceImpl<HrmAchievementsResultTemplateLevelMapper, HrmAchievementsResultTemplateLevel> implements IHrmAchievementsResultTemplateLevelService {

    @Override
    public List<HrmAchievementsResultTemplateLevel> queryListByTemplateId(Long templateId) {
        return lambdaQuery().select().eq(HrmAchievementsResultTemplateLevel::getResultTemplateId, templateId).orderByAsc(HrmAchievementsResultTemplateLevel::getSort).list();
    }
}
