package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("员工考核计划展示对象")
@Data
public class EmployeeAppraisalPlanVO {

    @ApiModelProperty(value = "员工考核id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核方案名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "考核员工id")
    private Long employeeId;

    @ApiModelProperty(value = "姓名")
    private String employeeName;

    @ApiModelProperty(value = "头像")
    private String img;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty(value = "聘用形式")
    private Integer employmentForms;

    @ApiModelProperty(value = "考核周期类型1:月度 2：季度 3:上半年、4:下半年、5:全年、6:其他")
    private Integer appraisalCycleType;

    @ApiModelProperty(value = "考核周期")
    private String appraisalCycle;

    @ApiModelProperty(value = "季度")
    private String quarter;

    @ApiModelProperty(value = "阶段类型")
    private Integer stageType;

    @ApiModelProperty(value = "考核状态")
    private Integer appraisalStatus;

    @ApiModelProperty(value = "考核阶段状态")
    private Integer stageStatus;

    @ApiModelProperty(value = "评分")
    private Integer score;

    @ApiModelProperty(value = "考核结果")
    private String level;

    @ApiModelProperty(value = "当前阶段")
    private Integer stageSort;

    @ApiModelProperty(value = "能否进行目标确认驳回")
    private Boolean canRejectTarget;
}
