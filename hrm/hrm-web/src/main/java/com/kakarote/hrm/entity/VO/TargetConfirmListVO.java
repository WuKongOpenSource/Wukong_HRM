package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetConfirmListVO {

    @ApiModelProperty("员工绩效id")
    private Long employeeAppraisalId;

    @ApiModelProperty("绩效id")
    private Long appraisalId;

    @ApiModelProperty("员工id")
    private Long employeeId;

    @ApiModelProperty("员工名称")
    private String employeeName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("部门")
    private String deptName;

    @ApiModelProperty("考核名称")
    private String appraisalName;

    @Override
    public String toString() {
        return "TargetConfirmListVO{" +
                "employeeAppraisalId=" + employeeAppraisalId +
                ", appraisalId=" + appraisalId +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", deptName='" + deptName + '\'' +
                ", appraisalName='" + appraisalName + '\'' +
                '}';
    }
}
