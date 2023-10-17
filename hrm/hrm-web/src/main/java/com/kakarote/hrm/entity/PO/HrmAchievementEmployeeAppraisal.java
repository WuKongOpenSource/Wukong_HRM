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
 * 员工绩效考核
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_employee_appraisal")
@ApiModel(value = "HrmAchievementEmployeeAppraisal对象", description = "员工绩效考核")
public class HrmAchievementEmployeeAppraisal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "employee_appraisal_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeAppraisalId;

    @ApiModelProperty(value = "员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "绩效id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appraisalId;

    @ApiModelProperty(value = "考核状态 1 待填写 2 待目标确认 3 待评定 4 待结果确认 5 终止绩效")
    private Integer status;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "考核结果")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long levelId;

    @ApiModelProperty(value = "结果阅读状态 0 未读 1 已读")
    private Integer readStatus;

    @ApiModelProperty(value = "跟进员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long followUpEmployeeId;

    @ApiModelProperty(value = "跟进员工排序")
    private Integer followSort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否为草稿 0否 1是")
    private Integer isDraft;

}
