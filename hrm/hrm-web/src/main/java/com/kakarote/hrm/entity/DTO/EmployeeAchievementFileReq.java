package com.kakarote.hrm.entity.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("删除员工绩效档案考核记录对象")
public class EmployeeAchievementFileReq {
    @ApiModelProperty("员工id")
    private Long employeeId;
    @ApiModelProperty("操作对象id列表")
    private List<Long> ids;
}
