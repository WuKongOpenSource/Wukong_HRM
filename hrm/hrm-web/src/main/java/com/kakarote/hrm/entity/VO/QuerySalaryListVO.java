package com.kakarote.hrm.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuerySalaryListVO {

    private Long sEmpRecordId;

    @ApiModelProperty("年")
    private Integer year;

    @ApiModelProperty("月")
    private Integer month;

    @ApiModelProperty("开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startTime;

    @ApiModelProperty("结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;

    @ApiModelProperty("应发工资")
    private String shouldSalary;

    @ApiModelProperty("个人所得税")
    private String personalTax;

    @ApiModelProperty("实发工资")
    private String realSalary;

    @Override
    public String toString() {
        return "QuerySalaryListVO{" +
                "sEmpRecordId=" + sEmpRecordId +
                ", year=" + year +
                ", month=" + month +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shouldSalary='" + shouldSalary + '\'' +
                ", personalTax='" + personalTax + '\'' +
                ", realSalary='" + realSalary + '\'' +
                '}';
    }
}
