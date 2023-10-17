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
 * 考核维度
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievements_assessment_dimension")
@ApiModel(value = "HrmAchievementsAssessmentDimension对象", description = "绩效管理-考核模板-维度表")
public class HrmAchievementsAssessmentDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考核维度id")
    @TableId(value = "dimension_id", type = IdType.ASSIGN_ID)
    private Long dimensionId;

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "允许员工填写1：是 0：否")
    private Boolean isAllowEdit;

    @ApiModelProperty(value = "模板id")
    private Long templateId;

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
