package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateSalaryBO {

    @ApiModelProperty("员工薪资记录id")
    private Long sEmpRecordId;

    @ApiModelProperty("薪资项")
    private Map<Integer, String> salaryValues;

    @Override
    public String toString() {
        return "UpdateSalaryBO{" +
                "sEmpRecordId=" + sEmpRecordId +
                ", salaryValues=" + salaryValues +
                '}';
    }
}
