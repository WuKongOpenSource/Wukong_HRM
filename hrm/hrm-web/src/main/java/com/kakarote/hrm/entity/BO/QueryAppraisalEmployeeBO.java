package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@ApiModel("考核人员列表查询对象")
@EqualsAndHashCode(callSuper = false)
public class QueryAppraisalEmployeeBO extends PageEntity {
    @ApiModelProperty("考核计划id")
    private Long appraisalPlanId;
    @ApiModelProperty("员工姓名")
    private String employeeName;
    @ApiModelProperty("聘用状态")
    private List<Integer> employStatus;
}
