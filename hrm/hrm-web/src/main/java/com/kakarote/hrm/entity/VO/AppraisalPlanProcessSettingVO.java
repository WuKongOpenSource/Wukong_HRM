package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("考核计划流程设置展示对象")
@Data
public class AppraisalPlanProcessSettingVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "指标制定类型1:统一制定2：员工填写")
    private Integer quotaSettingType;

    @ApiModelProperty(value = "目标确认：1：开启 0:未开启")
    private Boolean targetConfirmation;

    @ApiModelProperty(value = "结果审核")
    private Boolean resultAudit;

    @ApiModelProperty(value = "结果确认")
    private Boolean resultConfirmation;

    @ApiModelProperty(value = "超期未处理类型")
    private Integer beOverdueType;

    @ApiModelProperty(value = "超期未处理天数")
    private Integer overdueDays;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "目标确认对象")
    private ProcessSettingTargetConfirmationVO processSettingTargetConfirmationVO;

    @ApiModelProperty(value = "评分流程")
    private List<ProcessSettingScoringVO> processSettingScoringVOList;

    @ApiModelProperty(value = "结果审核")
    private List<ProcessSettingResultAuditVO> processSettingResultAuditVOList;

    @ApiModelProperty(value = "结果确认")
    private List<ProcessSettingResultConfirmationVO> processSettingResultConfirmationVOList;
}
