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
 * 绩效考核结果等级列表
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievements_result_template_level")
@ApiModel(value = "HrmAchievementsResultTemplateLevel对象", description = "绩效管理-结构模板等级表")
public class HrmAchievementsResultTemplateLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "level_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "结果模板等级id")
    private Long levelId;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "分数下限")
    private Integer scoreLowerLimit;

    @ApiModelProperty(value = "分数上限")
    private Integer scoreUpperLimit;

    @ApiModelProperty(value = "系数")
    private Double coefficient;

    @ApiModelProperty(value = "结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
