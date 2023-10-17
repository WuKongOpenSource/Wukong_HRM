package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
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
 * 每日出勤班次
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-19
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("wk_hrm_attendance_date_shift")
@ApiModel(value = "HrmAttendanceDateShift对象", description = "每日出勤班次")
public class HrmAttendanceDateShift implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户班次id")
    @TableId(value = "user_shift_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userShiftId;

    @ApiModelProperty("出勤时间")
    private LocalDateTime userShiftTime;

    @ApiModelProperty("员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty("班次id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shiftId;

    @ApiModelProperty("班次类型（0 休息 1早晚打卡 2 分段打卡）")
    private Integer shiftType;

    @ApiModelProperty("班次名称")
    private String shiftName;

    @ApiModelProperty("班次时长（分钟）")
    private Integer shiftHours;

    @ApiModelProperty("上班时间1")
    private String start1;

    @ApiModelProperty("下班时间1")
    private String end1;

    @ApiModelProperty("上班时间2")
    private String start2;

    @ApiModelProperty("下班时间2")
    private String end2;

    @ApiModelProperty("上班时间3")
    private String start3;

    @ApiModelProperty("下班时间3")
    private String end3;

    @ApiModelProperty("上班最早打卡时间1")
    private String advanceCard1;

    @ApiModelProperty("上班最晚打卡时间1")
    private String lateCard1;

    @ApiModelProperty("上班最早打卡时间2")
    private String advanceCard2;

    @ApiModelProperty("上班最晚打卡时间2")
    private String lateCard2;

    @ApiModelProperty("上班最早打卡时间3")
    private String advanceCard3;

    @ApiModelProperty("上班最晚打卡时间3")
    private String lateCard3;

    @ApiModelProperty("下班最早打卡时间1")
    private String earlyCard1;

    @ApiModelProperty("下班最晚打卡时间1")
    private String postponeCard1;

    @ApiModelProperty("下班最早打卡时间2")
    private String earlyCard2;

    @ApiModelProperty("下班最晚打卡时间2")
    private String postponeCard2;

    @ApiModelProperty("下班最早打卡时间3")
    private String earlyCard3;

    @ApiModelProperty("下班最晚打卡时间3")
    private String postponeCard3;

    @ApiModelProperty("是否设置休息时间（0否 1是）")
    private Integer restTimeStatus;

    @ApiModelProperty("休息开始时间")
    private String restStartTime;

    @ApiModelProperty("休息结束时间")
    private String restEndTime;

    @ApiModelProperty("是否是默认配置（0否 1是）")
    private Integer isDefaultSetting;

    @ApiModelProperty("生效时间")
    private LocalDateTime effectTime;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新人id")
    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}
