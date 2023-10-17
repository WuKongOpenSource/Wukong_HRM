package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@ApiModel("查询团队打卡明细")
@EqualsAndHashCode(callSuper = false)
public class QueryTeamAttendanceDailyDetailBO extends PageEntity {

    @ApiModelProperty("搜索")
    private String search;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "当前日期")
    private LocalDate currentDate;

    @ApiModelProperty(value = "打卡状态 0正常 1迟到 2早退 3缺卡 4旷工 5外勤 6请假 7加班 -1 全部")
    private Integer clockStatus;

}
