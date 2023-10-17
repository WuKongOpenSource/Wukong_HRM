package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("考核计划员工列表展示对象")
@Data
public class AppraisalPlanOfEmployeeVO {
    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "员工考核计划id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "考核方案名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "考核员工id")
    private Long employeeId;

    @ApiModelProperty(value = "姓名")
    private String employeeName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "聘用形式 1 正式 2 非正式")
    private Integer employmentForms;

    @ApiModelProperty(value = "聘用形式名称 1 正式 2 非正式")
    private String employmentFormsName;

    @ApiModelProperty(value = "考核状态")
    private Integer stageStatus;

    @ApiModelProperty(value = "考核状态")
    private Integer stageType;

    @ApiModelProperty(value = "考核状态名称")
    private String stageStatusName;

    @ApiModelProperty(value = "待处理人")
    private String stageUsers;

    @ApiModelProperty(value = "评分")
    private Integer score;

    @ApiModelProperty(value = "考核结果")
    private String level;

}
