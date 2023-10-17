package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AchievementsTemplateBO;
import com.kakarote.hrm.entity.BO.QueryAchievementsTemplateBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentTemplate;
import com.kakarote.hrm.entity.VO.AchievementsTemplatePageVO;
import com.kakarote.hrm.entity.VO.AchievementsTemplateVO;

/**
 * <p>
 * 绩效管理-考核模板 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface IHrmAchievementsAssessmentTemplateService extends BaseService<HrmAchievementsAssessmentTemplate> {

    //新增或更新模板
    Long addOrUpdate(AchievementsTemplateBO achievementsTemplateBO);

    /**
     * 查看考核模板列表
     *
     * @param search
     * @return
     */
    BasePage<AchievementsTemplatePageVO> queryPageList(QueryAchievementsTemplateBO search);

    /**
     * 根据id列表删除数据
     *
     * @param operationReq
     */
    void deleteByIdList(OperationReq operationReq);

    /**
     * 查询模板详情
     *
     * @param templateId
     * @return
     */
    AchievementsTemplateVO information(Long templateId);
}
