package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("考勤打卡列表")
public class QueryAttendancePageVO {

    @ApiModelProperty(value = "打卡记录id")
    private Long clockId;

    @ApiModelProperty(value = "打卡人")
    private Long clockEmployeeId;

    @ApiModelProperty(value = "打卡时间")
    private LocalDateTime clockTime;

    @ApiModelProperty(value = "打卡类型 1 上班打卡 2 下班打卡")
    private Integer clockType;

    @ApiModelProperty(value = "上班时间")
    private LocalDateTime attendanceTime;

    @ApiModelProperty(value = "打卡类型 1手机端打卡 2HR添加 3.自动打卡")
    private Integer type;

    @ApiModelProperty(value = "打卡状态 0 正常 1 迟到 2 早退 4 加班")
    private Integer clockStatus;

    @ApiModelProperty(value = "打卡阶段")
    private Integer clockStage;

    @ApiModelProperty(value = "考勤地址")
    private String address;

    @ApiModelProperty(value = "经度")
    private String lng;

    @ApiModelProperty(value = "维度")
    private String lat;

    @ApiModelProperty(value = "wifi名称")
    private String ssid;

    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ApiModelProperty(value = "是否外勤（0否 1是）")
    private Integer isOutWork;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "员工名称")
    private String employeeName;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "岗位")
    private String post;

}
