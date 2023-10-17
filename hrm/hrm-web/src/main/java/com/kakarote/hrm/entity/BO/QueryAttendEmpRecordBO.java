package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("考勤-员工记录")
public class QueryAttendEmpRecordBO {

    @ApiModelProperty(value = "时间区间")
    private List<LocalDate> times;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

}
