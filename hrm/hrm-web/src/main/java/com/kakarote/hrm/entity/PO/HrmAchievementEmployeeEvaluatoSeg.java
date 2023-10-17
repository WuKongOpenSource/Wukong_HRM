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
 * 员工绩效考核项评定表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_employee_evaluato_seg")
@ApiModel(value = "HrmAchievementEmployeeEvaluatoSeg对象", description = "员工绩效考核项评定表")
public class HrmAchievementEmployeeEvaluatoSeg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "employee_evaluato_seg_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeEvaluatoSegId;

    @ApiModelProperty(value = "员工端考核id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeAppraisalId;

    @ApiModelProperty(value = "评定id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeEvaluatoId;

    @ApiModelProperty(value = "考核项id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long segId;

    @ApiModelProperty(value = "评定人id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "评语")
    private String evaluate;

    private Integer status;

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

    @ApiModelProperty(value = "评定人")
    @TableField(exist = false)
    private String employeeName;


}
