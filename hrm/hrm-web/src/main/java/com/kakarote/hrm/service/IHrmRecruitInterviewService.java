package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.SetInterviewResultBO;
import com.kakarote.hrm.entity.BO.SetRecruitInterviewBO;
import com.kakarote.hrm.entity.PO.HrmRecruitInterview;

import java.util.List;

/**
 * <p>
 * 面试表 服务类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface IHrmRecruitInterviewService extends BaseService<HrmRecruitInterview> {

    /**
     * 安排面试
     *
     * @param setRecruitInterviewBO
     */
    OperationLog setInterview(SetRecruitInterviewBO setRecruitInterviewBO);

    /**
     * 填写面试结果
     *
     * @param setInterviewResultBO
     */
    OperationLog setInterviewResult(SetInterviewResultBO setInterviewResultBO);

    /**
     * 批量安排面试
     *
     * @param setRecruitInterviewBO
     * @return
     */
    List<OperationLog> addBatchInterview(SetRecruitInterviewBO setRecruitInterviewBO);
}
