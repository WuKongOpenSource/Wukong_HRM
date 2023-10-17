package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("考核模板保存对象")
@Data
public class AchievementsTemplateBO {

    @ApiModelProperty(value = "考核模板id")
    private Long templateId;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "指标说明")
    private String templateIllustrate;

    @ApiModelProperty(value = "总分计算")
    private Integer scoreCalculation;

    @ApiModelProperty(value = "评分上限类型")
    private Integer upperLimitType;

    @ApiModelProperty(value = "评分上限")
    private Integer upperLimitScore;

    @ApiModelProperty(value = "考核维度列表")
    private List<AchievementsDimensionBO> achievementsDimensionBOList;
}
