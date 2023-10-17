package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppraisalEmployeeInfoVO {
    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "考核周期")
    private String appraisalCycle;

    @ApiModelProperty(value = "目标填写")
    private String targetFillUser;

    @ApiModelProperty(value = "目标确认")
    private String targetConfirmation;

    @ApiModelProperty(value = "员工自评")
    private String selfEvaluation;

    @ApiModelProperty(value = "是否必填")
    private Boolean requiredSetting;

    @ApiModelProperty(value = "驳回权限")
    private Boolean rejectAuthority;

    @ApiModelProperty(value = "评分上限")
    private Double upperLimitScore;

    @ApiModelProperty(value = "当前用户是否是最后评分")
    private Boolean isEnd;

    @ApiModelProperty(value = "绩效考核模板维度列表")
    private List<AchievementsDimensionVO> dimensionVOList;

    @ApiModelProperty("员工指标评分记录列表")
    private List<ScoreRecordVO> scoreRecordVOList;

}
