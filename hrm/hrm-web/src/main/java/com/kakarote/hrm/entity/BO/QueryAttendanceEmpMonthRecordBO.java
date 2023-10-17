package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class QueryAttendanceEmpMonthRecordBO extends PageEntity {

    @ApiModelProperty("搜索")
    private String search;

    @ApiModelProperty("部门ids")
    private List<Long> deptIds;

    @ApiModelProperty(value = "时间区间")
    private List<LocalDate> times;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;

    @ApiModelProperty(value = "是否全勤 0否 1是")
    private Integer isFullAttendance;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

}
