package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetSalaryGroupBO {
    @ApiModelProperty(value = "薪资组id")
    private Long groupId;

    @ApiModelProperty(value = "薪资组名称")
    private String groupName;

    @ApiModelProperty(value = "部门范围")
    private List<Long> deptIds;

    @ApiModelProperty(value = "员工范围")
    private List<Long> employeeIds;

    @ApiModelProperty(value = "计税规则id")
    private Long ruleId;

    @Override
    public String toString() {
        return "SetSalaryGroupBO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", deptIds=" + deptIds +
                ", employeeIds=" + employeeIds +
                ", ruleId=" + ruleId +
                '}';
    }
}
