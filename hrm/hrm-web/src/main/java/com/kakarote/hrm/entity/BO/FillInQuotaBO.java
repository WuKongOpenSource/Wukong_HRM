package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("指标填写对象")
public class FillInQuotaBO {

    @ApiModelProperty(value = "考核计划")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "指标填写-考核维度")
    private List<AppraisalEmployeeDimensionBO> appraisalEmployeeDimensionBOList;

}
