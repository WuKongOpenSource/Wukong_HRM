package com.kakarote.hrm.entity.BO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class QuerySalaryListByEmailTokenBO extends QuerySalaryPageListBO {

    private String emailToken;

}
