package com.kakarote.hrm.entity.BO;

import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("每日打卡明细")
public class QueryAttendanceDailyDetailBO {

    @ApiModelProperty("员工id")
    private Long employeeId;

    @ApiModelProperty(value = "当前日期")
    private LocalDate currentDate;

    @ApiModelProperty(value = "考勤组")
    private HrmAttendanceGroup hrmAttendanceGroup;

    @ApiModelProperty(value = "批量查询 0否1是")
    private Integer multi;
}
