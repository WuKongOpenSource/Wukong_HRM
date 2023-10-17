package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("绩效考核模板")
@Data
public class AchievementsTemplateVO {

    @ApiModelProperty(value = "考核模板id")
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

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "修改人")
    private String updateUserName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "绩效考核模板维度列表")
    private List<AchievementsDimensionVO> dimensionVOList;
}
