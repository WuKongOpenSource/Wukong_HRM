package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTeamAttendanceDetailVO {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "员工头像")
    private String img;

    @ApiModelProperty(value = "上班打卡状态1")
    private Integer startStatus1;

    @ApiModelProperty(value = "上班打卡状态2")
    private Integer startStatus2;

    @ApiModelProperty(value = "上班打卡状态3")
    private Integer startStatus3;

    @ApiModelProperty(value = "下班打卡状态1")
    private Integer endStatus1;

    @ApiModelProperty(value = "下班打卡状态2")
    private Integer endStatus2;

    @ApiModelProperty(value = "下班打卡状态3")
    private Integer endStatus3;

    @Override
    public String toString() {
        return "QueryTeamAttendanceDetailVO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", img='" + img + '\'' +
                ", startStatus1=" + startStatus1 +
                ", startStatus2=" + startStatus2 +
                ", startStatus3=" + startStatus3 +
                ", endStatus1=" + endStatus1 +
                ", endStatus2=" + endStatus2 +
                ", endStatus3=" + endStatus3 +
                '}';
    }
}
