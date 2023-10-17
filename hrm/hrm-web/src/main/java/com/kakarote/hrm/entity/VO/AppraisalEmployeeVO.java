package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核人员列表")
public class AppraisalEmployeeVO {
    @ApiModelProperty("员工id")
    private Long employeeId;
    @ApiModelProperty("员工姓名")
    private String employeeName;
    @ApiModelProperty("部门")
    private String deptName;
    @ApiModelProperty("自评人")
    private String raterName;
}
