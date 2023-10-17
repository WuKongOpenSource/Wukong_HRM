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
 * 考核计划-指标配置-基础表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan_quota_setting")
@ApiModel(value = "HrmAppraisalPlanQuotaSetting对象", description = "考核计划-指标配置-基础表")
public class HrmAppraisalPlanQuotaSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "quota_setting_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "指标id")
    private Long quotaSettingId;

    @ApiModelProperty(value = "总分计算")
    private Integer scoreCalculation;

    @ApiModelProperty(value = "评分上限类型")
    private Integer upperLimitType;

    @ApiModelProperty(value = "评分上限")
    private Double upperLimitScore;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新人")
    private Long updateUserId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
