package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("员工月度状态明细")
public class QueryAttendanceEmpDetailVO {

    @ApiModelProperty(value = "时间列表")
    private List<Map<String, Object>> dateList;

    @ApiModelProperty(value = "开始时间")
    private LocalDate startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDate endTime;

}
