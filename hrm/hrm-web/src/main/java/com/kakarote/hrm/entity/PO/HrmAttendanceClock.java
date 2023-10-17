package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 打卡记录表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_attendance_clock")
@ApiModel(value = "HrmAttendanceClock对象", description = "打卡记录表")
public class HrmAttendanceClock implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "打卡记录id")
    @TableId(value = "clock_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long clockId;

    @ApiModelProperty(value = "打卡人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long clockEmployeeId;


    @ApiModelProperty(value = "打卡时间")
    private LocalDateTime clockTime;

    @ApiModelProperty(value = "打卡类型 1 上班打卡 2 下班打卡")
    private Integer clockType;

    @ApiModelProperty(value = "上班日期")
    private LocalDateTime attendanceTime;

    @ApiModelProperty(value = "打卡类型 1手机端打卡 2手工录入")
    private Integer type;

    @ApiModelProperty(value = "打卡状态 0 正常 1 迟到 2 早退  4 加班")
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

    @ApiModelProperty("员工名称")
    @TableField(exist = false)
    private String employeeName;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
