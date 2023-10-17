package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("")
public class QueryAttendanceEmpDetailBO {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "年")
    private Integer year;

    @ApiModelProperty(value = "月")
    private Integer month;

}
