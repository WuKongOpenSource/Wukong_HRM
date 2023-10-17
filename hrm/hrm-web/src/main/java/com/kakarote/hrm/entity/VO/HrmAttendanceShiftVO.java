package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HrmAttendanceShiftVO {

    @ApiModelProperty(value = "班次id")
    private Long shiftId;

    @ApiModelProperty(value = "班次类型（0 休息 1早晚打卡 2 分段打卡）")
    private Integer shiftType;

    @ApiModelProperty(value = "班次名称")
    private String shiftName;

    @ApiModelProperty(value = "班次时长（分钟）")
    private Integer shiftHours;

    @ApiModelProperty(value = "上班时间1")
    private String start1;

    @ApiModelProperty(value = "下班时间1")
    private String end1;

    @ApiModelProperty(value = "上班时间2")
    private String start2;

    @ApiModelProperty(value = "下班时间2")
    private String end2;

    @ApiModelProperty(value = "上班时间3")
    private String start3;

    @ApiModelProperty(value = "下班时间3")
    private String end3;

    @ApiModelProperty(value = "上班最早打卡时间1")
    private String advanceCard1;

    @ApiModelProperty(value = "上班最晚打卡时间1")
    private String lateCard1;

    @ApiModelProperty(value = "上班最早打卡时间2")
    private String advanceCard2;

    @ApiModelProperty(value = "上班最晚打卡时间2")
    private String lateCard2;

    @ApiModelProperty(value = "上班最早打卡时间3")
    private String advanceCard3;

    @ApiModelProperty(value = "上班最晚打卡时间3")
    private String lateCard3;

    @ApiModelProperty(value = "下班最早打卡时间1")
    private String earlyCard1;

    @ApiModelProperty(value = "下班最晚打卡时间1")
    private String postponeCard1;

    @ApiModelProperty(value = "下班最早打卡时间2")
    private String earlyCard2;

    @ApiModelProperty(value = "下班最晚打卡时间2")
    private String postponeCard2;

    @ApiModelProperty(value = "下班最早打卡时间3")
    private String earlyCard3;

    @ApiModelProperty(value = "下班最晚打卡时间3")
    private String postponeCard3;

    @ApiModelProperty(value = "是否设置休息时间（0否 1是）")
    private Integer restTimeStatus;

    @ApiModelProperty(value = "休息开始时间")
    private String restStartTime;

    @ApiModelProperty(value = "休息结束时间")
    private String restEndTime;

    @ApiModelProperty(value = "是否是默认配置（0否 1是）")
    private Integer isDefaultSetting;

    @ApiModelProperty("考勤班次时间")
    private String attendanceShiftDate;

    @Override
    public String toString() {
        return "HrmAttendanceShiftVO{" +
                "shiftId=" + shiftId +
                ", shiftType=" + shiftType +
                ", shiftName='" + shiftName + '\'' +
                ", shiftHours=" + shiftHours +
                ", start1='" + start1 + '\'' +
                ", end1='" + end1 + '\'' +
                ", start2='" + start2 + '\'' +
                ", end2='" + end2 + '\'' +
                ", start3='" + start3 + '\'' +
                ", end3='" + end3 + '\'' +
                ", advanceCard1='" + advanceCard1 + '\'' +
                ", lateCard1='" + lateCard1 + '\'' +
                ", advanceCard2='" + advanceCard2 + '\'' +
                ", lateCard2='" + lateCard2 + '\'' +
                ", advanceCard3='" + advanceCard3 + '\'' +
                ", lateCard3='" + lateCard3 + '\'' +
                ", earlyCard1='" + earlyCard1 + '\'' +
                ", postponeCard1='" + postponeCard1 + '\'' +
                ", earlyCard2='" + earlyCard2 + '\'' +
                ", postponeCard2='" + postponeCard2 + '\'' +
                ", earlyCard3='" + earlyCard3 + '\'' +
                ", postponeCard3='" + postponeCard3 + '\'' +
                ", restStartTime='" + restStartTime + '\'' +
                ", restEndTime='" + restEndTime + '\'' +
                ", isDefaultSetting=" + isDefaultSetting +
                ", attendanceShiftDate='" + attendanceShiftDate + '\'' +
                '}';
    }
}
