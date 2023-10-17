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
 * 打卡规则表
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("wk_hrm_attendance_rule")
@ApiModel(value = "HrmAttendanceRule对象", description = "打卡规则表")
public class HrmAttendanceRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "打卡规则id")
    @TableId(value = "attendance_rule_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceRuleId;

    @ApiModelProperty(value = "打卡规则名称")
    private String attendanceRuleName;

    @ApiModelProperty(value = "迟到规则计算方式")
    private Integer lateRuleMethod;

    @ApiModelProperty(value = "迟到扣款金额")
    private BigDecimal lateDeductMoney;

    @ApiModelProperty(value = "早退规则计算方式")
    private Integer earlyRuleMethod;


    @ApiModelProperty(value = "早退扣款金额")
    private BigDecimal earlyDeductMoney;

    @ApiModelProperty(value = "缺卡规则计算方式")
    private Integer misscardRuleMethod;

    @ApiModelProperty(value = "缺卡扣款金额")
    private BigDecimal misscardDeductMoney;

    @ApiModelProperty(value = "旷工规则计算方式")
    private Integer absenteeismRuleMethod;

    @ApiModelProperty(value = "旷工扣款金额")
    private BigDecimal absenteeismDeductMoney;

    @ApiModelProperty(value = "是否个性化设置{0 否 1是}")
    private Integer isPersonalization;

    @ApiModelProperty(value = "迟到的总分钟或总次数")
    private Integer lateMinutesOrCounts;

    @ApiModelProperty(value = "早退的总分钟或总次数")
    private Integer earlyMinutesOrCounts;

    @ApiModelProperty(value = "是否是默认配置(0否 1是)")
    private Integer isDefaultSetting;

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
