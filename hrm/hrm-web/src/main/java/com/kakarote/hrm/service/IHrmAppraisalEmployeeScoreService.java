package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeScore;

import java.util.List;

/**
 * <p>
 * 员工考核计划-评分 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-30
 */
public interface IHrmAppraisalEmployeeScoreService extends BaseService<HrmAppraisalEmployeeScore> {

    /**
     * 查询员工评分记录
     *
     * @param appraisalEmployeeId
     * @return
     */
    List<HrmAppraisalEmployeeScore> queryEmployeeScoreList(Long appraisalEmployeeId);

    /**
     * 根据评分阶段id驳回评分
     *
     * @param appraisalStageId
     */
    void rejectScoreByStageId(Long appraisalEmployeeId, List<Long> appraisalStageId);
}
