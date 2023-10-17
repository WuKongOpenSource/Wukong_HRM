package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("员工考核计划阶段处理对象")
public class EmployeeAppraisalStageBO {
    private Long stageUserId;
    private String stageUserName;
}
