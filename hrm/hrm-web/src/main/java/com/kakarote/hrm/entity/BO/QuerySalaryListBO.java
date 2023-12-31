package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuerySalaryListBO extends PageEntity {

    private Long employeeId;

    @Override
    public String toString() {
        return "QuerySalaryListBO{" +
                "employeeId=" + employeeId +
                '}';
    }
}
