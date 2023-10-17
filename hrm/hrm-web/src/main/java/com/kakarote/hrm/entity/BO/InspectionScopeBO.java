package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("新建考核计划-考核范围")
public class InspectionScopeBO {
    @ApiModelProperty("新建考核计划-考核范围类型1:员工与部门 2：聘用形式")
    private Integer type;
    @ApiModelProperty("员工id列表")
    private List<Long> employeeIds;
    @ApiModelProperty("员工名称列表")
    private String employeeNames;
    @ApiModelProperty("部门id列表")
    private List<Long> deptIds;
    @ApiModelProperty("部门名称列表")
    private String deptNames;
    @ApiModelProperty(value = "聘用形式1 正式 2 非正式")
    private Integer employType;
    @ApiModelProperty(value = "聘用形式二级")
    private List<Integer> employeeStatus;
}
