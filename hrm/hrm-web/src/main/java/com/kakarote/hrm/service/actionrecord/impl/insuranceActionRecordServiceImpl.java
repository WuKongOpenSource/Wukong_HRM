package com.kakarote.hrm.service.actionrecord.impl;

import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.core.common.enums.LanguageFieldEnum;
import com.kakarote.hrm.constant.HrmLanguageEnum;
import com.kakarote.hrm.entity.PO.HrmInsuranceMonthRecord;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.stereotype.Service;

@Service("insuranceActionRecordService")
public class insuranceActionRecordServiceImpl extends AbstractHrmActionRecordService {

    public Content computeInsuranceDataLog(HrmInsuranceMonthRecord insuranceMonthRecord) {
        String detail = "生成" + insuranceMonthRecord.getYear() + "=" + insuranceMonthRecord.getTitle();
        String transDetail = HrmLanguageEnum.GENERATE.getFieldFormat() + insuranceMonthRecord.getYear() + "=" + insuranceMonthRecord.getTitle();
        return new Content(insuranceMonthRecord.getTitle(), detail, transDetail, BehaviorEnum.SAVE);
    }

    public Content deleteInsurance(HrmInsuranceMonthRecord insuranceMonthRecord) {
        String detail = "删除了" + insuranceMonthRecord.getYear() + "=" + insuranceMonthRecord.getTitle();
        String transDetail = LanguageFieldEnum.ACTIONRECORDD_DELETED.getFieldFormat() + insuranceMonthRecord.getYear() + "=" + insuranceMonthRecord.getTitle();
        return new Content(insuranceMonthRecord.getTitle(), detail, transDetail, BehaviorEnum.DELETE);
    }
}
