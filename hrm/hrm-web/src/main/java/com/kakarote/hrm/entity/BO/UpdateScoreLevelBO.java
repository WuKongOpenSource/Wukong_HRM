package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateScoreLevelBO {

    @ApiModelProperty(value = "员工绩效id")
    private Long employeeAppraisalId;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "考核结果")
    private Long levelId;

    @ApiModelProperty(value = "原因")
    private String reason;

    @Override
    public String toString() {
        return "UpdateScoreLevelBO{" +
                "employeeAppraisalId=" + employeeAppraisalId +
                ", score=" + score +
                ", levelId=" + levelId +
                ", reason='" + reason + '\'' +
                '}';
    }
}
