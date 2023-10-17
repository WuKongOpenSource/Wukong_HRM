package com.kakarote.hrm.entity.BO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("指标填写-维度对象")
public class AppraisalEmployeeDimensionBO {

    @ApiModelProperty(value = "维度Id")
    private Long dimensionId;

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "指标填写-指标列表")
    private List<AppraisalEmployeeQuotaBO> appraisalEmployeeQuotaBOList;
}
