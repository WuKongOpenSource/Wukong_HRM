package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class QueryEmpInsuranceMonthVO {
    @ApiModelProperty(value = "员工id")
    @TableId(value = "employee_id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "入职时间")
    private LocalDateTime entryTime;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("参保城市")
    private String city;

    @ApiModelProperty("参保方案")
    private String schemeName;

    @ApiModelProperty(value = "个人社保金额")
    private BigDecimal personalInsuranceAmount;

    @ApiModelProperty(value = "个人公积金金额")
    private BigDecimal personalProvidentFundAmount;

    @ApiModelProperty(value = "公司社保金额")
    private BigDecimal corporateInsuranceAmount;

    @ApiModelProperty(value = "公司社保金额")
    private BigDecimal corporateProvidentFundAmount;

    @Override
    public String toString() {
        return "QueryEmpInsuranceMonthVO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", entryTime=" + entryTime +
                ", deptName='" + deptName + '\'' +
                ", city='" + city + '\'' +
                ", schemeName='" + schemeName + '\'' +
                ", personalInsuranceAmount=" + personalInsuranceAmount +
                ", personalProvidentFundAmount=" + personalProvidentFundAmount +
                ", corporateInsuranceAmount=" + corporateInsuranceAmount +
                ", corporateProvidentFundAmount=" + corporateProvidentFundAmount +
                '}';
    }
}
