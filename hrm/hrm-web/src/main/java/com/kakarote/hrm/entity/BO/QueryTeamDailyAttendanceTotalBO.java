package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询团队每日打卡汇总")
public class QueryTeamDailyAttendanceTotalBO {

    @ApiModelProperty(value = "当前日期")
    private String currentDate;

}
