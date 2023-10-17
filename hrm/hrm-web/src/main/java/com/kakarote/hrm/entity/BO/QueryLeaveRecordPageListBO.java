package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("查询请假记录BO")
@EqualsAndHashCode(callSuper = false)
public class QueryLeaveRecordPageListBO extends PageEntity {

    @ApiModelProperty("搜索")
    private String search;

    @ApiModelProperty("员工id")
    private Long employeeId;

    @ApiModelProperty(value = "请假时间")
    private List<LocalDate> times;

    @ApiModelProperty("部门ids")
    private List<Long> deptIds;

    @ApiModelProperty("请假类型")
    private List<String> leaveTypes;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;
}
