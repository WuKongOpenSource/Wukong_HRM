package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryHistorySalaryDetailBO extends PageEntity {

    private Long sRecordId;

    private String employeeName;

    private String jobNumber;

    private Long deptId;

    @Override
    public String toString() {
        return "QueryHistorySalaryDetailBO{" +
                "sRecordId=" + sRecordId +
                ", employeeName='" + employeeName + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", deptId=" + deptId +
                '}';
    }
}
