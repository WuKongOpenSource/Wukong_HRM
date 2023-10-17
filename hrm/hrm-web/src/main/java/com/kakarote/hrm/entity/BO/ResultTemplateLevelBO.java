package com.kakarote.hrm.entity.BO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("结果模板等级列表保存实体")
public class ResultTemplateLevelBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "分数下限")
    private Double scoreLowerLimit;

    @ApiModelProperty(value = "分数上限")
    private Double scoreUpperLimit;

    @ApiModelProperty(value = "系数")
    private Double coefficient;
}
