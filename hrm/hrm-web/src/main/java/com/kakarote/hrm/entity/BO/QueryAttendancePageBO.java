package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("查询考勤打卡条件")
@EqualsAndHashCode(callSuper = false)
public class QueryAttendancePageBO extends PageEntity {

    @ApiModelProperty("部门ids")
    private List<Long> deptIds;

    @ApiModelProperty("打卡来源")
    private List<Integer> types;

    @ApiModelProperty("搜索")
    private String search;

    @ApiModelProperty(value = "打卡状态 0 正常 1 迟到 2 早退 3 外勤 4 加班")
    private Integer clockStatus;

    @ApiModelProperty(value = "打卡类型 1 上班打卡 2 下班打卡")
    private Integer clockType;

    @ApiModelProperty("打卡地点")
    private String address;

    @ApiModelProperty(value = "打卡时间")
    private List<LocalDate> times;
}
