package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("指标审核对象")
public class QuotaAuditBO {
    @ApiModelProperty("员工绩效考核id")
    private Long appraisalEmployeeId;
    @ApiModelProperty("驳回原因")
    private String reason;
}
