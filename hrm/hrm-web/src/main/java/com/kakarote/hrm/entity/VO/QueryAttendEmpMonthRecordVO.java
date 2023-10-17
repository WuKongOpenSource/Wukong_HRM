package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("员工月度考勤记录")
public class QueryAttendEmpMonthRecordVO {

    @ApiModelProperty(value = "应出勤天数")
    private Integer attendDays;

    @ApiModelProperty(value = "实际出勤天数")
    private Integer actualDays;

    @ApiModelProperty(value = "迟到时长（分钟）")
    private Integer lateMinute;

    @ApiModelProperty(value = "迟到次数")
    private Integer lateCount;

    @ApiModelProperty(value = "早退时长（分钟）")
    private Integer earlyMinute;

    @ApiModelProperty(value = "早退次数")
    private Integer earlyCount;

    @ApiModelProperty(value = "旷工天数")
    private BigDecimal absenteeismDays;

    @ApiModelProperty(value = "缺卡次数")
    private Integer misscardCount;

    @ApiModelProperty(value = "请假天数")
    private BigDecimal leaveDays;

    @ApiModelProperty(value = "外勤天数")
    private Integer outDays;

    @ApiModelProperty(value = "加班次数")
    private Integer overTimeCount;

    @ApiModelProperty(value = "是否全勤")
    private Integer isFullAttendance;

}
