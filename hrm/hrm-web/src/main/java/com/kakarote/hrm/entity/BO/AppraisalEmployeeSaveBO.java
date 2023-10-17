package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("绩效考核员工保存对象")
public class AppraisalEmployeeSaveBO {
    @ApiModelProperty("考核计划id")
    private Long appraisalPlanId;
    @ApiModelProperty("员工id列表")
    private List<Long> employeeIdList;
}
