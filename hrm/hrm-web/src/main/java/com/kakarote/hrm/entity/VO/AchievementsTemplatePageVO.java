package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("考核模板列表展示对象")
public class AchievementsTemplatePageVO {
    @ApiModelProperty(value = "考核模板id")
    private Long templateId;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板说明")
    private String templateIllustrate;

    @ApiModelProperty(value = "维度")
    private Integer dimensionNum;

    @ApiModelProperty(value = "指标")
    private Integer quotaNum;

    @ApiModelProperty(value = "总分")
    private Integer upperLimitScore;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    //    @JsonFormat(pattern = "yyyy-mm-dd hh:mm")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
