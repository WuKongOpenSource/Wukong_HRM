package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("指标评分")
@Data
public class QuotaScoreBO {

    @ApiModelProperty("员工绩效id")
    private Long appraisalEmployeeId;

    @ApiModelProperty("评语")
    private String comments;

    @ApiModelProperty("指标评分列表")
    private List<QuotaScoreInfoBO> quotaScoreInfoBOList;
}
