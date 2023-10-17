package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("员工评分记录")
public class ScoreRecordVO {
    @ApiModelProperty("员工姓名")
    private String employeeName;

    @ApiModelProperty("评分结果")
    private String result;

    @ApiModelProperty("评分权重")
    private Double weight;

    @ApiModelProperty("评语")
    private String comments;

    @ApiModelProperty("是否可填写")
    private Boolean canFill = false;
}
