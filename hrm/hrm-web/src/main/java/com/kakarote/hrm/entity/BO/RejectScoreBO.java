package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("评分驳回对象")
public class RejectScoreBO {

    @ApiModelProperty("员工考核绩效id")
    private Long appraisalEmployeeId;

    @ApiModelProperty("驳回原因")
    private String rejectReason;
}
