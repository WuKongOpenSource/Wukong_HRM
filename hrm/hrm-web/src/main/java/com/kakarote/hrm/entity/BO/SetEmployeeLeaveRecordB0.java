package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class SetEmployeeLeaveRecordB0 {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "审批id")
    private Long examineId;

    @ApiModelProperty(value = "请假类型")
    private String leaveType;

    @ApiModelProperty(value = "请假开始时间")
    private LocalDateTime leaveStartTime;

    @ApiModelProperty(value = "请假结束时间")
    private LocalDateTime leaveEndTime;

    @ApiModelProperty(value = "请假时长")
    private BigDecimal leaveDay;

    @ApiModelProperty(value = "请假理由")
    private String leaveReason;

    @ApiModelProperty(value = "备注")
    private String remark;
}
