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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 考核结果评定人
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_appraisal_evaluators")
@ApiModel(value = "HrmAchievementAppraisalEvaluators对象", description = "考核结果评定人")
public class HrmAchievementAppraisalEvaluators implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "evaluators_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long evaluatorsId;

    @ApiModelProperty(value = "考核id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appraisalId;

    @ApiModelProperty(value = "1 员工本人 2 直属上级 3 所在部门负责人 4 上级部门负责人 5 指定目标确认人")
    private Integer type;

    @ApiModelProperty(value = "指定确认人id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "权重")
    private BigDecimal weight;

    private Integer sort;

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

    @TableField(exist = false)
    private String employeeName;

    @TableField(exist = false)
    private Integer status;


}
