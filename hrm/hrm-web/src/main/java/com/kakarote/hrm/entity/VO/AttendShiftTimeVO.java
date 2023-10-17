package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("班次时间明细")
public class AttendShiftTimeVO {

    @ApiModelProperty(value = "上班时间")
    private String start;

    @ApiModelProperty(value = "下班时间")
    private String end;

    @ApiModelProperty(value = "上班开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "上班最早打卡时间")
    private LocalDateTime advanceTime;

    @ApiModelProperty(value = "上班最晚打卡时间")
    private LocalDateTime lateTime;

    @ApiModelProperty(value = "下班最晚打卡时间")
    private LocalDateTime postponeTime;

    @ApiModelProperty(value = "下班最早打卡时间")
    private LocalDateTime earlyTime;

    @ApiModelProperty(value = "下班结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "是否能添加")
    private Integer status;

}
