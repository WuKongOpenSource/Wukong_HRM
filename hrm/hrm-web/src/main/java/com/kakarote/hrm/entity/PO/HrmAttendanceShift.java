package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 班次表
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("wk_hrm_attendance_shift")
@ApiModel(value = "HrmAttendanceShift对象", description = "班次表")
public class HrmAttendanceShift implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "班次id")
    @TableId(value = "shift_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
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

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

}
