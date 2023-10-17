package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询当前打卡类型")
public class QueryMyCurrentLastClockVO {

    @ApiModelProperty(value = "打卡类型 1 上班打卡 2 下班打卡")
    private Integer clockType;

    @ApiModelProperty(value = "打卡阶段")
    private Integer clockStage;

    @ApiModelProperty(value = "打卡状态 0未到时间 1正常打卡 2.更新打卡 3迟到打卡 4早退打卡")
    private Integer clockStatus;

    @ApiModelProperty(value = "是否请假 0否 1是")
    private Integer leaveStatus;

}
