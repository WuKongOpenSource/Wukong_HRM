package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("考核维度保存对象")
public class AchievementsDimensionBO {

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "允许员工填写true：是 false：否")
    private Boolean isAllowEdit = false;

    @ApiModelProperty(value = "考核指标列表")
    private List<AchievementsPointBO> achievementsPointBOList;
}
