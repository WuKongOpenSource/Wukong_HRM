package com.kakarote.hrm.constant.appraisal;

public enum InspectionTypeEnum {
    EMPLOYEE(1, "员工"),
    DEPT(2, "部门");
    private Integer value;
    private String name;

    InspectionTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
