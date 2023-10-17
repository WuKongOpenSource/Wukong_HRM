package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "高级筛选基础条件对象")
public class QueryEmployeePageListBO extends PageEntity {

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "性别 1 男 2 女")
    private Integer sex;

    @ApiModelProperty(value = "入职时间")
    private List<LocalDate> entryTime;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty(value = "转正日期")
    private List<LocalDate> becomeTime;

    @ApiModelProperty(value = "工作地点")
    private String workAddress;

    @ApiModelProperty(value = "聘用形式 1 正式 2 非正式")
    private Integer employmentForms;

    @ApiModelProperty(value = "员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包 9待离职 10已离职 11 在职 12 全职")
    private Integer status;

    @ApiModelProperty("人事概况 1 入职 2 离职 3 转正 4 调岗 5 待入职 6 待离职")
    private Integer employeeSurvey;

    @ApiModelProperty("代办提醒 2 待离职 3 合同到期 4 待转正 5 待入职 6 生日")
    private Integer toDoRemind;

    @ApiModelProperty("员工id(导出使用)")
    private List<Long> employeeIds;

    @ApiModelProperty(value = "排序字段")
    private String sortField;
    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;

    @Override
    public String toString() {
        return "QueryEmployeePageListBO{" +
                "employeeName='" + employeeName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", sex=" + sex +
                ", entryTime=" + entryTime +
                ", jobNumber='" + jobNumber + '\'' +
                ", deptId=" + deptId +
                ", post='" + post + '\'' +
                ", becomeTime=" + becomeTime +
                ", workAddress='" + workAddress + '\'' +
                ", employmentForms=" + employmentForms +
                ", status=" + status +
                ", employeeSurvey=" + employeeSurvey +
                ", toDoRemind=" + toDoRemind +
                ", employeeIds=" + employeeIds +
                ", sortField='" + sortField + '\'' +
                ", order=" + order +
                '}';
    }
}
