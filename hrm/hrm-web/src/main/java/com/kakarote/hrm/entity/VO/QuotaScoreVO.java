package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("员工指标评分详情")
public class QuotaScoreVO {

    @ApiModelProperty("员工指标评分详情列表")
    private List<QuotaEmployeeScoreVO> quotaEmployeeScoreVOList;

    @ApiModelProperty("员工指标评分记录列表")
    private List<ScoreRecordVO> scoreRecordVOList;
}
