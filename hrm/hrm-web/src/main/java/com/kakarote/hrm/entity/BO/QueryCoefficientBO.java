package com.kakarote.hrm.entity.BO;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryCoefficientBO {
    private List<Long> employeeIdList;
    private Integer year;
    private Integer month;
}
