package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("指标评分详情")
public class QuotaScoreInfoBO {

    @ApiModelProperty(value = "指标id")
    private Long quotaId;

    @ApiModelProperty(value = "评分")
    private Long score;

    @ApiModelProperty("评语")
    private String comments;
}
