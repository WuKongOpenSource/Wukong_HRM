package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryAchievementsResultTemplateBO;
import com.kakarote.hrm.entity.BO.ResultTemplateBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplate;
import com.kakarote.hrm.entity.VO.AchievementsResultTemplateVO;
import com.kakarote.hrm.entity.VO.ResultTemplatePageVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
public interface IHrmAchievementsResultTemplateService extends BaseService<HrmAchievementsResultTemplate> {

    Long saveOrUpdate(ResultTemplateBO resultTemplateBO);

    /**
     * 查看模板结果详细
     *
     * @param resultTemplateId
     * @return
     */
    AchievementsResultTemplateVO information(Long resultTemplateId);

    /**
     * 查看模板结果列表
     *
     * @param search
     * @return
     */
    BasePage<ResultTemplatePageVO> queryPageList(QueryAchievementsResultTemplateBO search);

    /**
     * 根据id列表删除数据
     *
     * @param operationReq
     */
    void deleteByIdList(OperationReq operationReq);
}
