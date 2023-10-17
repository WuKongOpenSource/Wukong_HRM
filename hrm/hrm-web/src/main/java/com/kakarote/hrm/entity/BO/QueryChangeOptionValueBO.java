package com.kakarote.hrm.entity.BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryChangeOptionValueBO {

    private Long templateId;

    private Long employeeId;

    @Override
    public String toString() {
        return "QueryChangeOptionValueBO{" +
                "templateId=" + templateId +
                ", employeeId=" + employeeId +
                '}';
    }
}
