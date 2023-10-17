package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划查询对象")
public class AppraisalPlanReq {
    @ApiModelProperty("考核计划id")
    private Long appraisalPlanId;
}
