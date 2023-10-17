package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("打卡概况")
public class QueryAttendanceEmpMonthDetailVO {

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty(value = "时间列表")
    private List<Map<String, Object>> dateList;

}
