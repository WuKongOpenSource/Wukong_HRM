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
 * 考核指标
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievements_assessment_dimension_quota")
@ApiModel(value = "HrmAchievementsAssessmentDimensionQuota对象", description = "绩效管理-考核模板-维度指标表")
public class HrmAchievementsAssessmentDimensionQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "维度指标id")
    @TableId(value = "quota_id", type = IdType.ASSIGN_ID)
    private Long quotaId;

    @ApiModelProperty(value = "指标名称")
    private String quotaName;

    @ApiModelProperty(value = "指标说明")
    private String quotaIllustrate;

    @ApiModelProperty(value = "考核标准")
    private String standard;

    @ApiModelProperty(value = "指标权重")
    private Double quotaWeight;

    @ApiModelProperty(value = "评分方式")
    private Integer scoreType;

    @ApiModelProperty(value = "维度id")
    private Long dimensionId;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人")
    private Long updateUserId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
