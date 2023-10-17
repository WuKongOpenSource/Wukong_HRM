package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.BO.InspectionScopeBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("考核计划设置信息")
public class AppraisalPlanSettingInfoVO {
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

    @ApiModelProperty(value = "考核模板名称")
    private String appraisalTemplateName;

    @ApiModelProperty(value = "是否同步到薪资")
    private Boolean syncToSalary;

    @ApiModelProperty(value = "引用的结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "引用的结果模板id")
    private String resultTemplateName;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "更新人")
    private Long updateUserName;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    @ApiModelProperty(value = "状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档")
    private Integer status;

    @ApiModelProperty(value = "指标设置")
    private AppraisalPlanQuotaSettingVO appraisalPlanQuotaSettingVO;

    @ApiModelProperty(value = "流程设置")
    private AppraisalPlanProcessSettingVO appraisalPlanProcessSettingVO;

    @ApiModelProperty(value = "结果设置")
    private AppraisalEmployeeResultSettingVO appraisalPlanResultSettingVO;
}
