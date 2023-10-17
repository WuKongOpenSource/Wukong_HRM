package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.HrmActionTypeEnum;
import com.kakarote.hrm.entity.BO.QueryRecordListBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalActionRecord;
import com.kakarote.hrm.entity.VO.QueryAppraisalRecordListVO;

import java.util.List;

/**
 * <p>
 * 绩效考核审核记录 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
public interface IHrmAppraisalActionRecordService extends BaseService<HrmAppraisalActionRecord> {
    /**
     * 保存操作记录
     *
     * @param actionTypeEnum 操作对象类型
     * @param behaviorEnum   行为类型
     * @param content        内容
     * @param typeId         类型id
     * @param batchId        文件批次id
     * @return
     */
    boolean saveRecord(HrmActionTypeEnum actionTypeEnum, HrmActionBehaviorEnum behaviorEnum, String content, String transContent, Long typeId, String batchId);


    /**
     * 查询操作记录列表
     *
     * @param queryRecordListBO
     * @return
     */
    List<QueryAppraisalRecordListVO> queryRecordList(QueryRecordListBO queryRecordListBO);

}
