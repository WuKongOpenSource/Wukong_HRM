package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("结果模板等级列表对象")
public class ResultTemplateLevelVO {

    @ApiModelProperty(value = "结果模板等级id")
    private Long levelId;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "分数下限")
    private Double scoreLowerLimit;

    @ApiModelProperty(value = "分数上限")
    private Double scoreUpperLimit;

    @ApiModelProperty(value = "系数")
    private Double coefficient;

    @ApiModelProperty(value = "结果模板id")
    private Long resultTemplateId;
}
