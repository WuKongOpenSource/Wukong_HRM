package com.kakarote.hrm.service.actionrecord.impl;

import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.hrm.constant.HrmLanguageEnum;
import com.kakarote.hrm.entity.PO.HrmSalaryMonthRecord;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.stereotype.Service;

@Service("salaryActionRecordService")
public class SalaryActionRecordServiceImpl extends AbstractHrmActionRecordService {

    public Content addNextMonthSalaryLog(HrmSalaryMonthRecord salaryMonthRecord) {
        String detail = "添加" + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        String transDetail = HrmLanguageEnum.ADD.getFieldFormat() + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        return new Content(salaryMonthRecord.getTitle(), detail, transDetail, BehaviorEnum.SAVE);
    }

    public Content computeSalaryDataLog(HrmSalaryMonthRecord salaryMonthRecord) {
        String detail = "核算" + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        String transDetail = HrmLanguageEnum.ACCOUNTING.getFieldFormat() + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        return new Content(salaryMonthRecord.getTitle(), detail, transDetail, BehaviorEnum.SAVE);
    }

    public Content deleteSalaryLog(HrmSalaryMonthRecord salaryMonthRecord) {
        String detail = "删除" + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        String transDetail = HrmLanguageEnum.DELETE.getFieldFormat() + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle();
        return new Content(salaryMonthRecord.getTitle(), detail, transDetail, BehaviorEnum.SAVE);
    }
}
