package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeAppealRecord;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-02
 */
public interface IHrmAppraisalEmployeeAppealRecordService extends BaseService<HrmAppraisalEmployeeAppealRecord> {

    /**
     * 更新处理状态
     *
     * @param appraisalEmployeeId
     * @param status
     */
    void updateAppealRecordStatus(Long appraisalEmployeeId, Integer status);

}
