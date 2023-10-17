package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuerySalarySlipListBO extends PageEntity {

    @ApiModelProperty(name = "开始时间", example = "yyyy-mm")
    private String startTime;

    @ApiModelProperty(name = "结束时间", example = "yyyy-mm")
    private String endTime;

    @ApiModelProperty(name = "年", example = "yyyy")
    private String year;

    @ApiModelProperty("排序类型 1 发放时间 2 实发工资")
    private Integer orderType;

    @ApiModelProperty("1 倒序 2 正序")
    private Integer order;


    @Override
    public String toString() {
        return "QuerySalarySlipListBO{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", year='" + year + '\'' +
                ", orderType=" + orderType +
                ", order=" + order +
                '}';
    }
}
