package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("指标详情查询对象")
public class QuotaInfoQueryBO {
    @ApiModelProperty("员工考核id")
    private Long appraisalEmployeeId;
    @ApiModelProperty("是否评分true / false")
    private Boolean isScoring;
}
