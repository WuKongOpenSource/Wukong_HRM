package com.kakarote.hrm.constant.appraisal;

/**
 * 指标类型枚举类
 */
public enum QuotaTypeEnum {
    SYSTEM_FORMULATE(1, "系统制定"),
    EMPLOYEE_FILL(2, "员工填写");
    private Integer value;
    private String name;

    QuotaTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
