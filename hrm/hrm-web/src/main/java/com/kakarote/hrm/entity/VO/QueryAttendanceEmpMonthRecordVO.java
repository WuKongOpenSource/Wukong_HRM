package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("员工月度汇总对象")
public class QueryAttendanceEmpMonthRecordVO {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty("入职日期")
    private LocalDate entryTime;

    @ApiModelProperty("员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包")
    private Integer status;

    @ApiModelProperty("工作城市")
    private String workCity;

    @ApiModelProperty(value = "司龄")
    private Integer companyAge;

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

    @ApiModelProperty(value = "考勤组名称")
    private String attendanceGroupName;

    @ApiModelProperty(value = "是否全勤")
    private Integer isFullAttendance;

}
