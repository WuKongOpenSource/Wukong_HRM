package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.BO.InspectionScopeBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("考核计划展示对象")
@Data
public class AppraisalPlanVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "考核范围")
    private List<InspectionScopeBO> inspectionScope;

    @ApiModelProperty(value = "考核范围")
    private String inspectionScopeName;

    @ApiModelProperty(value = "考核人数")
    private Integer appraisalPersons;

    @ApiModelProperty(value = "目标填写人数")
    private Integer fillPersons;

    @ApiModelProperty(value = "目标待确认人数")
    private Integer targetConfirmationPersons;

    @ApiModelProperty(value = "目标待确认人数")
    private Integer executingPersons;

    @ApiModelProperty(value = "自评人数")
    private Integer selfScoringPersons;

    @ApiModelProperty(value = "他人评分人数")
    private Integer otherScoringPersons;

    @ApiModelProperty(value = "结果审核人数")
    private Integer resultAuditPersons;

    @ApiModelProperty(value = "结果确认人数")
    private Integer resultConfirmationPersons;

    @ApiModelProperty(value = "聘用形式1 正式 2 非正式")
    private Integer employType;

    @ApiModelProperty(value = "聘用形式名称")
    private String employTypeNames;

    @ApiModelProperty(value = "员工状态")
    private String employeeStatus;

    @ApiModelProperty(value = "考核周期类型1:月度 2：季度 3:上半年、4:下半年、5:全年、6:其他")
    private Integer appraisalCycleType;

    @ApiModelProperty(value = "考核周期")
    private String appraisalCycle;

    @ApiModelProperty(value = "季度")
    private String quarter;

    @ApiModelProperty(value = "考核开始时间")
    private Date startTime;

    @ApiModelProperty(value = "考核结束时间")
    private Date endTime;

    @ApiModelProperty(value = "考核说明")
    private String appraisalIllustrate;

    @ApiModelProperty(value = "考核模板id")
    private Long appraisalTemplateId;

    @ApiModelProperty(value = "是否同步到薪资")
    private Boolean syncToSalary;

    @ApiModelProperty(value = "引用的结果模板id")
    private Long resultTemplateId;

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

    @ApiModelProperty(value = "阶段状态：0：未开始 1：员工填写 2:目标确认3：自评分 4：他人评分 5：结果审核6：结果确认7：申诉确认：归档 9:执行中")
    private Integer stageStatus;

    @ApiModelProperty(value = "进行中可操作阶段状态： 1：开启评分 2：发起绩效面谈3：归档")
    private Integer operationStage;
}
