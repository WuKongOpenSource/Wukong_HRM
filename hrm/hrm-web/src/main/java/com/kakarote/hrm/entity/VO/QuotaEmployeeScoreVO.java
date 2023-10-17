package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("指标员工评分")
public class QuotaEmployeeScoreVO {
    @ApiModelProperty("指标id")
    private Long quotaId;
    @ApiModelProperty("评分人")
    private String employeeName;
    @ApiModelProperty("评分")
    private Long score;
    @ApiModelProperty("评语")
    private String comments;
    @ApiModelProperty("是否可填写")
    private Boolean canFill = false;
}
