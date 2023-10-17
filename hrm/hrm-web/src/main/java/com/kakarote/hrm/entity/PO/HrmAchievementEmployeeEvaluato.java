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
 * 员工绩效结果评定表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_employee_evaluato")
@ApiModel(value = "HrmAchievementEmployeeEvaluato对象", description = "员工绩效结果评定表")
public class HrmAchievementEmployeeEvaluato implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "employee_evaluato_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeEvaluatoId;

    @ApiModelProperty(value = "员工端考核id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeAppraisalId;

    @ApiModelProperty(value = "绩效id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appraisalId;

    @ApiModelProperty(value = "确认人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "权重")
    private BigDecimal weight;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "考核等级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long levelId;

    @ApiModelProperty(value = "评语")
    private String evaluate;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    private Integer sort;

    @ApiModelProperty(value = "0 待评定 1 已评定 2 驳回目标 3 驳回评定")
    private Integer status;


}
