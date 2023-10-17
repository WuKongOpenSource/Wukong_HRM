package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAppraisalAppealPendingRecord;

import java.util.List;

/**
 * <p>
 * 员工绩效考核申诉确认待处理节点记录 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-15
 */
public interface IHrmAppraisalAppealPendingRecordService extends BaseService<HrmAppraisalAppealPendingRecord> {

    /**
     * 添加待处理记录
     *
     * @param appraisalEmployeeId
     * @param stageId
     */
    void addAppealPendingRecord(Long appraisalEmployeeId, Long stageId);


    /**
     * 查询已逾期的任务
     *
     * @return
     */
    List<HrmAppraisalAppealPendingRecord> queryOverduePendingRecordList();
}
