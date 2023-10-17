package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-结果设置保存对象")
public class AppraisalPlanResultSettingBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "等级id")
    private Long levelId;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "分数下限")
    private Double scoreLowerLimit;

    @ApiModelProperty(value = "分数上限")
    private Double scoreUpperLimit;

    @ApiModelProperty(value = "系数")
    private Double coefficient;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

}
