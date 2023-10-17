package com.kakarote.hrm.entity.VO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuerySalaryArchivesByIdVO {

    private Long employeeId;

    private List<ChangeSalaryOptionVO> salaryOptions;

    private String total;

    @Override
    public String toString() {
        return "QuerySalaryArchivesByIdVO{" +
                "employeeId=" + employeeId +
                ", salaryOptions=" + salaryOptions +
                ", total='" + total + '\'' +
                '}';
    }
}
