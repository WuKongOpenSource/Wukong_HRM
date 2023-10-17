package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("员工指标评分详情")
public class PreQuotaScoreVO {
    @ApiModelProperty("当前人评分结果")
    private Double score;
    @ApiModelProperty("当前人等级")
    private String levelName;
    @ApiModelProperty("评分")
    private Double scoreOfTotal;
    @ApiModelProperty("等级")
    private String levelNameOfTotal;
}
