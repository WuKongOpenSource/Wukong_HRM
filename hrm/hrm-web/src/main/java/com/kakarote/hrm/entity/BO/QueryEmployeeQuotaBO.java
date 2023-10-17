package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("员工考核计划查询对象")
@Data
@EqualsAndHashCode(callSuper = false)
public class QueryEmployeeQuotaBO extends PageEntity {
    @ApiModelProperty("员工id")
    private Long employeeId;
    @ApiModelProperty("员工姓名")
    private String employeeName;
    @ApiModelProperty("考核方案id")
    private Long appraisalPlanId;
    @ApiModelProperty("考核方案名称")
    private String appraisalPlanName;
    @ApiModelProperty("等级名称")
    private String level;
    @ApiModelProperty("阶段状态：0：未开始 1：员工填写 2:目标确认3：自评分 4：他人评分 5：结果审核6：结果确认 7：归档")
    private Integer status;
}
