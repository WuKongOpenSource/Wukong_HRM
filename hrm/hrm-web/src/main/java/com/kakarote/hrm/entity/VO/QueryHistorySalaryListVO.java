package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class QueryHistorySalaryListVO {

    @TableId(value = "s_record_id")
    @ApiModelProperty("历史薪资id")
    private Long sRecordId;

    @ApiModelProperty("报表标题")
    private String title;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty(value = "计薪人数")
    private Integer num;

    @ApiModelProperty(value = "预计应发工资")
    private BigDecimal expectedPaySalary;

    @ApiModelProperty(value = "个人所得税")
    private BigDecimal personalTax;

    @ApiModelProperty(value = "预计实发工资")
    private BigDecimal realPaySalary;

    @ApiModelProperty(value = "个人社保")
    private BigDecimal personalInsuranceAmount;

    @ApiModelProperty(value = "个人公积金")
    private BigDecimal personalProvidentFundAmount;

    @ApiModelProperty(value = "企业社保")
    private BigDecimal corporateInsuranceAmount;

    @ApiModelProperty(value = "企业公积金")
    private BigDecimal corporateProvidentFundAmount;

    @ApiModelProperty(value = "审批记录id")
    private Long examineRecordId;

    @ApiModelProperty("状态状态 0待审核、1通过、2拒绝、3审核中 4:撤回 5 未提交")
    private Integer checkStatus;

    @ApiModelProperty(value = "创建id")
    private Long createUserId;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    @Override
    public String toString() {
        return "QueryHistorySalaryListVO{" +
                "sRecordId=" + sRecordId +
                ", title='" + title + '\'' +
                ", num=" + num +
                ", expectedPaySalary=" + expectedPaySalary +
                ", personalTax=" + personalTax +
                ", realPaySalary=" + realPaySalary +
                ", personalInsuranceAmount=" + personalInsuranceAmount +
                ", personalProvidentFundAmount=" + personalProvidentFundAmount +
                ", corporateInsuranceAmount=" + corporateInsuranceAmount +
                ", corporateProvidentFundAmount=" + corporateProvidentFundAmount +
                ", examineRecordId=" + examineRecordId +
                ", checkStatus=" + checkStatus +
                ", createUserId=" + createUserId +
                '}';
    }
}
