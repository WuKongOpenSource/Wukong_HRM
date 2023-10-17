package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@ApiModel("待处理绩效查询列表")
@Data
@EqualsAndHashCode(callSuper = false)
public class QueryEmployeePendingAppraisalBO extends PageEntity {
    @ApiModelProperty("阶段：1：指标填写 2:目标确认 4：他人评分(包含自评分和他人评分) 5：结果审核6：结果确认 7申诉确认 8：归档 9 执行中")
    private Integer stage;
    @ApiModelProperty("最终查询阶段")
    private List<Integer> finalStage;
    @ApiModelProperty("是否已处理:1已处理，2：待处理 5已申诉")
    private Integer status;
    @ApiModelProperty("员工id")
    private Long employeeId;
}
