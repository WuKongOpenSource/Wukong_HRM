package com.kakarote.hrm.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class QueryInsurancePageListVO {
    @ApiModelProperty("员工社保记录id")
    private Long iEmpRecordId;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "职位")
    private String post;

    @ApiModelProperty("工号")
    private String jobNumber;

    @ApiModelProperty("入职时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime entryTime;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("参保城市")
    private String city;

    @ApiModelProperty(value = "社保方案id")
    private Long schemeId;

    @ApiModelProperty(value = "社保方案")
    private String schemeName;

    @ApiModelProperty(value = "个人社保金额")
    private BigDecimal personalInsuranceAmount;

    @ApiModelProperty(value = "个人公积金金额")
    private BigDecimal personalProvidentFundAmount;

    @ApiModelProperty(value = "公司社保金额")
    private BigDecimal corporateInsuranceAmount;

    @ApiModelProperty(value = "公司社保金额")
    private BigDecimal corporateProvidentFundAmount;

    private Integer isDel;

    @Override
    public String toString() {
        return "QueryInsurancePageListVO{" +
                "iEmpRecordId=" + iEmpRecordId +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", deptName='" + deptName + '\'' +
                ", post='" + post + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", entryTime=" + entryTime +
                ", mobile='" + mobile + '\'' +
                ", city='" + city + '\'' +
                ", schemeId=" + schemeId +
                ", schemeName='" + schemeName + '\'' +
                ", personalInsuranceAmount=" + personalInsuranceAmount +
                ", personalProvidentFundAmount=" + personalProvidentFundAmount +
                ", corporateInsuranceAmount=" + corporateInsuranceAmount +
                ", corporateProvidentFundAmount=" + corporateProvidentFundAmount +
                ", isDel=" + isDel +
                '}';
    }
}
