package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAppraisalAppealPendingRecord;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployee;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanProcessSetting;
import com.kakarote.hrm.mapper.HrmAppraisalAppealPendingRecordMapper;
import com.kakarote.hrm.service.IHrmAppraisalAppealPendingRecordService;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import com.kakarote.hrm.service.IHrmAppraisalPlanProcessSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 员工绩效考核申诉确认待处理节点记录 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-15
 */
@Service
public class HrmAppraisalAppealPendingRecordServiceImpl extends BaseServiceImpl<HrmAppraisalAppealPendingRecordMapper, HrmAppraisalAppealPendingRecord> implements IHrmAppraisalAppealPendingRecordService {

    @Autowired
    private IHrmAppraisalPlanProcessSettingService processSettingService;

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @Override
    public void addAppealPendingRecord(Long appraisalEmployeeId, Long stageId) {
        HrmAppraisalEmployee appraisalEmployee = appraisalEmployeeService.getById(appraisalEmployeeId);
        HrmAppraisalPlanProcessSetting appraisalPlanProcessSetting = processSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
        Integer overdueType = appraisalPlanProcessSetting.getBeOverdueType();
        Integer overdueDays = appraisalPlanProcessSetting.getOverdueDays();//逾期天数

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime overdueTime = now.plusDays(overdueDays);

        HrmAppraisalAppealPendingRecord appraisalAppealPendingRecord = new HrmAppraisalAppealPendingRecord();
        appraisalAppealPendingRecord.setAppraisalEmployeeId(appraisalEmployeeId);
        appraisalAppealPendingRecord.setOverdueType(overdueType);
        appraisalAppealPendingRecord.setStageId(stageId);
        appraisalAppealPendingRecord.setAppraisalPlanId(appraisalEmployee.getAppraisalPlanId());
        appraisalAppealPendingRecord.setOverdueTime(overdueTime);
        appraisalAppealPendingRecord.setCreateTime(new Date());
        save(appraisalAppealPendingRecord);
    }


    @Override
    public List<HrmAppraisalAppealPendingRecord> queryOverduePendingRecordList() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return lambdaQuery().le(HrmAppraisalAppealPendingRecord::getOverdueTime, localDateTime).list();
    }


}
