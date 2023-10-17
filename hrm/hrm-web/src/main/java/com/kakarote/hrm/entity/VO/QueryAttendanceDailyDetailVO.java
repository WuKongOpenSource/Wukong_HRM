package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.PO.HrmEmployeeLeaveRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("每日考勤详情")
public class QueryAttendanceDailyDetailVO {

    @ApiModelProperty(value = "班次")
    private HrmAttendanceShiftVO hrmAttendanceShiftVO;

    @ApiModelProperty(value = "考勤明细")
    private List<QueryAttendanceRecordVO> attendanceEmpDailyRecordList;

    @ApiModelProperty(value = "请假明细")
    private List<HrmEmployeeLeaveRecord> hrmEmployeeLeaveRecordLists;

    @ApiModelProperty(value = "员工名称")
    private String employeeName;

    @ApiModelProperty(value = "当前日期")
    private LocalDate currentDate;

}
