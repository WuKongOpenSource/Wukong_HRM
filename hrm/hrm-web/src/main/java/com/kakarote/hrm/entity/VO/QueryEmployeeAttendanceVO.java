package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QueryEmployeeAttendanceVO {
    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "职位")
    private String post;

    @ApiModelProperty("入职日期")
    private LocalDate entryTime;

    @ApiModelProperty("员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包")
    private Integer status;

    @ApiModelProperty("工作城市")
    private String workCity;

    @ApiModelProperty(value = "司龄")
    private Integer companyAge;

    @Override
    public String toString() {
        return "QueryEmployeeAttendanceVO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", deptName='" + deptName + '\'' +
                ", post='" + post + '\'' +
                ", entryTime=" + entryTime +
                ", status=" + status +
                ", workCity='" + workCity + '\'' +
                ", companyAge=" + companyAge +
                '}';
    }
}
