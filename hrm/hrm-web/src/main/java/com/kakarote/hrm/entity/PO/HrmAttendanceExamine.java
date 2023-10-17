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
 * 考勤审批设置
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-07
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("wk_hrm_attendance_examine")
@ApiModel(value = "HrmAttendanceExamine对象", description = "考勤审批设置")
public class HrmAttendanceExamine implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("审批id")
    @TableId(value = "attendance_examine_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceExamineId;

    @ApiModelProperty("关联审批字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long examineFieldId;

    @ApiModelProperty("类型字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long typeFieldId;

    @ApiModelProperty("开始时间字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long startTimeFieldId;

    @ApiModelProperty("结束时间字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long endTimeFieldId;

    @ApiModelProperty("天数字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long durationFieldId;

    @ApiModelProperty("备注字段id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long remarkFieldId;

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
