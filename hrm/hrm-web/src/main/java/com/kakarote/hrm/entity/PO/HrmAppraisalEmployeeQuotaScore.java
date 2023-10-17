package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工考核计划评分表
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_quota_score")
@ApiModel(value = "HrmAppraisalEmployeeQuotaScore对象", description = "员工考核计划评分表")
public class HrmAppraisalEmployeeQuotaScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "评分id")
    private Long scoreId;

    @ApiModelProperty(value = "指标id")
    private Long quotaId;

    @ApiModelProperty(value = "评分")
    private Long score;

    @ApiModelProperty(value = "评语")
    private String comments;

    @ApiModelProperty(value = "员工考核id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "评分人")
    private Long rater;

    @TableField(exist = false)
    @ApiModelProperty(value = "评分人名称")
    private String raterName;
}
