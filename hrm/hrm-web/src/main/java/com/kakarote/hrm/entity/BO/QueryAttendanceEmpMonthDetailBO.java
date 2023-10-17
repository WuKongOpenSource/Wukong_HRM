package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("打卡概况查询")
public class QueryAttendanceEmpMonthDetailBO {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "时间区间")
    private List<LocalDate> times;

    @ApiModelProperty(value = "状态 -1全部")
    private Integer status;

    @ApiModelProperty(value = "是否包含今日 0否 1是")
    private Integer includeToday;
}
