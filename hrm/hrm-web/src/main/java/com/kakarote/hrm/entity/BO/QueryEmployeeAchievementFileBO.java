package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("员工考核计划查询对象")
@Data
@EqualsAndHashCode(callSuper = false)
public class QueryEmployeeAchievementFileBO extends PageEntity {

    @ApiModelProperty("员工姓名")
    private String employeeName;

    @ApiModelProperty("考核绩效名称")
    private String appraisalPlanName;
}
