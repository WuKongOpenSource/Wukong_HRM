package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryTeamDailyAttendanceTotalVO {

    @ApiModelProperty(value = "应出勤员工数")
    private Integer attendEmpCount;

    @ApiModelProperty(value = "迟到员工数")
    private Integer lateEmpCount;

    @ApiModelProperty(value = "早退员工数")
    private Integer earlyEmpCount;

    @ApiModelProperty(value = "加班员工数")
    private Integer overTimeEmpCount;

    @ApiModelProperty(value = "旷工员工数")
    private Integer absenteeismEmpCount;

    @ApiModelProperty(value = "缺卡员工数")
    private Integer misscardEmpCount;

    @ApiModelProperty(value = "请假员工数")
    private Integer leaveEmpCount;

    @ApiModelProperty(value = "外勤员工数")
    private Integer outEmpCount;

    @Override
    public String toString() {
        return "QueryTeamDailyAttendanceTotalVO{" +
                "attendEmpCount=" + attendEmpCount +
                ", lateEmpCount=" + lateEmpCount +
                ", earlyEmpCount=" + earlyEmpCount +
                ", overTimeEmpCount=" + overTimeEmpCount +
                ", absenteeismEmpCount=" + absenteeismEmpCount +
                ", misscardEmpCount=" + misscardEmpCount +
                ", leaveEmpCount=" + leaveEmpCount +
                ", outEmpCount=" + outEmpCount +
                '}';
    }
}
