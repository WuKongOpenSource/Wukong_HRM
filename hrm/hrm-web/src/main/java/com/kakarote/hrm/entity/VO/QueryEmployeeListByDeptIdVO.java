package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueryEmployeeListByDeptIdVO {

    @ApiModelProperty(value = "员工id")
    @TableId(value = "employee_id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "职位")
    private String post;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "聘用形式 1 正式 2 非正式")
    private Integer employmentForms;

    @ApiModelProperty(value = "入职时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime entryTime;

    @Override
    public String toString() {
        return "QueryEmployeeListByDeptIdVO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", deptName='" + deptName + '\'' +
                ", post='" + post + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", employmentForms=" + employmentForms +
                ", entryTime=" + entryTime +
                '}';
    }
}
