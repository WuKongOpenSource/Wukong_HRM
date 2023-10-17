package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("查询外勤打卡")
public class QueryAttendanceOutCardBO extends PageEntity {

    @ApiModelProperty(value = "员工ID")
    private Long employeeId;

    @ApiModelProperty(value = "打卡时间")
    private List<LocalDate> times;

}
