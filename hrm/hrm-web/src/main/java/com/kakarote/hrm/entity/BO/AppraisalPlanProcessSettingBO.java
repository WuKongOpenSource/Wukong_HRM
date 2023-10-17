package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("考核计划-流程设置-保存对象")
public class AppraisalPlanProcessSettingBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "指标制定类型1:统一制定2：员工填写")
    private Integer quotaSettingType;

    @ApiModelProperty(value = "目标确认：true：开启 false:未开启,默认false")
    private Boolean targetConfirmation = false;

    @ApiModelProperty(value = "结果审核")
    private Boolean resultAudit = false;

    @ApiModelProperty(value = "结果确认")
    private Boolean resultConfirmation = false;

    @ApiModelProperty(value = "超期未处理类型")
    private Integer beOverdueType;

    @ApiModelProperty(value = "超期未处理天数")
    private Integer overdueDays;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "目标确认")
    private ProcessSettingTargetConfirmationBO processSettingTargetConfirmationBO;

    @ApiModelProperty(value = "评分流程")
    private List<ProcessSettingScoringBO> processSettingScoringBOList;

    @ApiModelProperty(value = "结果审核")
    private List<ProcessSettingResultAuditBO> processSettingResultAuditBOList;

    @ApiModelProperty(value = "结果确认")
    private List<ProcessSettingResultConfirmationBO> processSettingResultConfirmationBOList;

}
