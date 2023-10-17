package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
public class SalaryGroupPageListVO {

    @ApiModelProperty(value = "薪资组id")
    private Long groupId;

    @ApiModelProperty(value = "薪资组名称")
    private String groupName;

    @ApiModelProperty(value = "部门范围")
    private List<SimpleHrmDeptVO> deptList;

    @ApiModelProperty(value = "员工范围")
    private List<SimpleHrmEmployeeVO> employeeList;

    @ApiModelProperty(value = "月计薪标准")
    private String salaryStandard;

    @ApiModelProperty(value = "转正、调薪月规则")
    private String changeRule;

    @ApiModelProperty(value = "计税规则id")
    private Long ruleId;

    @ApiModelProperty(value = "计税规则")
    private String ruleName;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    @Override
    public String toString() {
        return "SalaryGroupPageListVO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", deptList=" + deptList +
                ", employeeList=" + employeeList +
                ", salaryStandard='" + salaryStandard + '\'' +
                ", changeRule='" + changeRule + '\'' +
                ", ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                '}';
    }
}
