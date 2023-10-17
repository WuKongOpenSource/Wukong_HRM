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
 * 绩效管理-考核模板
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievements_assessment_template")
@ApiModel(value = "HrmAchievementsAssessmentTemplate对象", description = "绩效管理-考核模板")
public class HrmAchievementsAssessmentTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考核模板id")
    @TableId(value = "template_id", type = IdType.ASSIGN_ID)
    private Long templateId;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板说明")
    private String templateIllustrate;

    @ApiModelProperty(value = "总分计算")
    private Integer scoreCalculation;

    @ApiModelProperty(value = "评分上限类型")
    private Integer upperLimitType;

    @ApiModelProperty(value = "评分上限")
    private Integer upperLimitScore;

    @ApiModelProperty(value = "维度数量")
    private Integer dimensionNum;

    @ApiModelProperty(value = "指标数量")
    private Integer quotaNum;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    @TableField(exist = false)
    private Long createUserName;

    @ApiModelProperty(value = "修改人")
    private Long updateUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "1:正常 0:删除")
    private Integer status;
}
