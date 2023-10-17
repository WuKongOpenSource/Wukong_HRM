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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 考勤组表
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("wk_hrm_attendance_group")
@ApiModel(value = "HrmAttendanceGroup对象", description = "考勤组表")
public class HrmAttendanceGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考勤组id")
    @TableId(value = "attendance_group_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceGroupId;

    @ApiModelProperty(value = "旧考勤组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long oldGroupId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工作时长")
    private BigDecimal dailyTime;

    @ApiModelProperty(value = "扣款规则id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceRuleId;

    @ApiModelProperty("是否启用定位打卡")
    private Integer isOpenPointCard;

    @ApiModelProperty("是否启用wifi打卡")
    private Integer isOpenWifiCard;

    @ApiModelProperty(value = "是否自动打卡（0否 1是）")
    private Integer isAutoCard;

    @ApiModelProperty(value = "考勤班组设置")
    private String shiftSetting;

    @ApiModelProperty(value = "是否法定节假日休息（0否 1是）")
    private Integer isRest;

    @ApiModelProperty(value = "特殊日期设置")
    private String specialDateSetting;

    @ApiModelProperty(value = "是否是默认配置（0否 1是）")
    private Integer isDefaultSetting;

    @ApiModelProperty(value = "是否历史配置 (0否 1是)")
    private Integer oldSetting;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;
}
