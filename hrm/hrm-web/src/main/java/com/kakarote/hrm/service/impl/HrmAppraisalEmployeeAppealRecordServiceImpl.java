package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeAppealRecord;
import com.kakarote.hrm.mapper.HrmAppraisalEmployeeAppealRecordMapper;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeAppealRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-02
 */
@Service
public class HrmAppraisalEmployeeAppealRecordServiceImpl extends BaseServiceImpl<HrmAppraisalEmployeeAppealRecordMapper, HrmAppraisalEmployeeAppealRecord> implements IHrmAppraisalEmployeeAppealRecordService {

    @Override
    public void updateAppealRecordStatus(Long appraisalEmployeeId, Integer status) {
        lambdaUpdate().set(HrmAppraisalEmployeeAppealRecord::getStatus, status).eq(HrmAppraisalEmployeeAppealRecord::getAppraisalEmployeeId, appraisalEmployeeId);//将申诉记录标记为已处理
    }
}
