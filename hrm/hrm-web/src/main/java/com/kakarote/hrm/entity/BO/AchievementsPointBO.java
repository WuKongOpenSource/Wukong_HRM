package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("考核指标保存对象")
@Data
public class AchievementsPointBO {

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

}
