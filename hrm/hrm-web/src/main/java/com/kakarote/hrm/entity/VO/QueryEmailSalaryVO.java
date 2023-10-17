package com.kakarote.hrm.entity.VO;

import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.PO.HrmSalaryMonthRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryEmailSalaryVO {
    private BasePage<QuerySalaryPageListVO> salaryPageListVO;
    private HrmSalaryMonthRecord salaryMonthRecord;
    private List<SalaryOptionHeadVO> salaryOptionHeadVOList;

    private Map<Integer, Long> employeeChangeNum;
}
