package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("考核计划基础信息保存对象")
@Data
public class AppraisalPlanBO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "考核范围")
    private List<InspectionScopeBO> inspectionScope;

    @ApiModelProperty(value = "考核周期类型1:月度 2：季度 3:上半年、4:下半年、5:全年、6:其他")
    private Integer appraisalCycleType;

    @ApiModelProperty(value = "考核周期")
    private String appraisalCycle;

    @ApiModelProperty(value = "季度")
    private String quarter;

    @ApiModelProperty(value = "考核说明")
    private String appraisalIllustrate;

    @ApiModelProperty(value = "考核模板id")
    private Long appraisalTemplateId;

    @ApiModelProperty(value = "指标设置")
    private AppraisalPlanQuotaSettingBO appraisalPlanQuotaSettingBO;

    @ApiModelProperty(value = "流程设置")
    private AppraisalPlanProcessSettingBO appraisalPlanProcessSettingBO;

    @ApiModelProperty(value = "结果设置")
    private AppraisalEmployeeResultSettingBO appraisalEmployeeResultSettingBO;
}
