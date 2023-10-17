package com.kakarote.hrm.entity.BO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakarote.hrm.entity.PO.HrmAchievementAppraisalEvaluators;
import com.kakarote.hrm.entity.PO.HrmAchievementAppraisalScoreLevel;
import com.kakarote.hrm.entity.PO.HrmAchievementAppraisalTargetConfirmors;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class SetAppraisalBO {
    @ApiModelProperty(value = "考核Id")
    private Long appraisalId;

    @ApiModelProperty(value = "考核名称")
    @NotBlank(message = "考核名称不能为空")
    private String appraisalName;

    @ApiModelProperty(value = "1 月 2 季 3 年 4 半年")
    private Integer cycleType;

    @ApiModelProperty(value = "考核开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty(value = "考核结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ApiModelProperty(value = "考核表模板id")
    private Long tableId;

    @ApiModelProperty(value = "考核目标填写人 1 本人")
    private Integer writtenBy;

    @ApiModelProperty(value = "考核结果确认人")
    private Collection<Long> resultConfirmors;

    @ApiModelProperty(value = "考评总分数")
    private BigDecimal fullScore;

    @ApiModelProperty(value = "是否开启强制分布 1 是 0 否")
    private Integer isForce;

    @ApiModelProperty(value = "考核员工")
    private Collection<Long> employeeIds;

    @ApiModelProperty(value = "考核部门")
    private Collection<Long> deptIds;

    @ApiModelProperty(value = "目标确认人")
    private List<HrmAchievementAppraisalTargetConfirmors> targetConfirmorsList;

    @ApiModelProperty(value = "结果确认人")
    private List<HrmAchievementAppraisalEvaluators> evaluatorsList;

    @ApiModelProperty(value = "分值等级")
    private List<HrmAchievementAppraisalScoreLevel> scoreLevelList;

    @Override
    public String toString() {
        return "SetAppraisalBO{" +
                "appraisalId=" + appraisalId +
                ", appraisalName='" + appraisalName + '\'' +
                ", cycleType=" + cycleType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", tableId=" + tableId +
                ", writtenBy=" + writtenBy +
                ", resultConfirmors=" + resultConfirmors +
                ", fullScore=" + fullScore +
                ", isForce=" + isForce +
                ", employeeIds=" + employeeIds +
                ", deptIds=" + deptIds +
                ", targetConfirmorsList=" + targetConfirmorsList +
                ", evaluatorsList=" + evaluatorsList +
                ", scoreLevelList=" + scoreLevelList +
                '}';
    }
}
