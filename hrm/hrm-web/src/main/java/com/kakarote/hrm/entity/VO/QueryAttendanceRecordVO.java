package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("打卡记录")
public class QueryAttendanceRecordVO {

    @ApiModelProperty(value = "打卡时间")
    private LocalDateTime clockTime;

    @ApiModelProperty(value = "班次类型（0 休息 1早晚打卡 2 分段打卡）")
    private Integer shiftType;

    @ApiModelProperty(value = "打卡类型 1 上班打卡 2 下班打卡")
    private Integer clockType;

    @ApiModelProperty(value = "上班日期")
    private LocalDateTime attendanceTime;

    @ApiModelProperty(value = "打卡类型 1手机端打卡 2HR添加 3.自动打卡")
    private Integer type;

    @ApiModelProperty(value = "打卡状态 0正常 1迟到 2早退 3缺卡 4旷工 5外勤 6请假 7加班 8出差 9休息")
    private Integer status;

    @ApiModelProperty(value = "打卡阶段")
    private Integer clockStage;

    @ApiModelProperty(value = "考勤地址")
    private String address;

    @ApiModelProperty(value = "考勤WiFi")
    private String wifi;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "分钟")
    private Integer minutes;

    @ApiModelProperty(value = "请假分钟")
    private Integer leaveMinutes;

    @ApiModelProperty(value = "请假类型")
    private String leaveType;

    @ApiModelProperty(value = "请假开始时间")
    private String leaveStartTime;

    @ApiModelProperty(value = "请假结束时间")
    private String leaveEndTime;

    @ApiModelProperty(value = "是否外勤")
    private Integer isOutCard;

}
