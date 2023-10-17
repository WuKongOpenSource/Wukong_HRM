package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.PO.HrmSalarySlipOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuerySalarySlipListVO {

    private Long id;

    private Integer year;

    private Integer month;

    private Integer readStatus;

    @ApiModelProperty("薪资项")
    private List<HrmSalarySlipOption> salarySlipOptionList;

    @Override
    public String toString() {
        return "QuerySalarySlipListVO{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", readStatus=" + readStatus +
                ", salarySlipOptionList=" + salarySlipOptionList +
                '}';
    }
}
