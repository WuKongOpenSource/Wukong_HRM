package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("员工班次考勤明细")
public class AttendEmpOverViewVO {

    @ApiModelProperty(value = "班次类型（0 休息 1早晚打卡 2 分段打卡）")
    private Integer shiftType;

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

    @ApiModelProperty(value = "迟到分钟1")
    private Integer late1;

    @ApiModelProperty(value = "迟到分钟2")
    private Integer late2;

    @ApiModelProperty(value = "迟到分钟3")
    private Integer late3;

    @ApiModelProperty(value = "早退分钟1")
    private Integer early1;

    @ApiModelProperty(value = "早退分钟2")
    private Integer early2;

    @ApiModelProperty(value = "早退分钟3")
    private Integer early3;

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

    @ApiModelProperty(value = "旷工分钟1")
    private Integer absenteeism1;

    @ApiModelProperty(value = "旷工分钟2")
    private Integer absenteeism2;

    @ApiModelProperty(value = "旷工分钟3")
    private Integer absenteeism3;

    @ApiModelProperty(value = "请假类型1")
    private String leaveType1;

    @ApiModelProperty(value = "请假结束1")
    private String leaveEnd1;

    @ApiModelProperty(value = "请假类型2")
    private String leaveType2;

    @ApiModelProperty(value = "请假结束2")
    private String leaveEnd2;

    @ApiModelProperty(value = "请假类型3")
    private String leaveType3;

    @ApiModelProperty(value = "请假结束3")
    private String leaveEnd3;

    @ApiModelProperty(value = "加班时长1")
    private Integer otTime1;

    @ApiModelProperty(value = "加班类型1")
    private Integer otType1;

    @ApiModelProperty(value = "加班时长2")
    private Integer otTime2;

    @ApiModelProperty(value = "加班类型2")
    private Integer otType2;

    @ApiModelProperty(value = "加班时长3")
    private Integer otTime3;

    @ApiModelProperty(value = "加班类型3")
    private Integer otType3;

    @ApiModelProperty(value = "是否外勤")
    private Integer isOutCard;

}
