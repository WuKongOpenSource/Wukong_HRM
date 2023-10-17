package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuerySalaryPageListBO extends PageEntity {

    @ApiModelProperty("薪资记录id")
    private Long sRecordId;

    private Long employeeId;

    private Long deptId;

    private Integer type;

    private String employeeName;

    @Override
    public String toString() {
        return "QuerySalaryPageListBO{" +
                "sRecordId=" + sRecordId +
                ", employeeId=" + employeeId +
                ", deptId=" + deptId +
                ", type=" + type +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
