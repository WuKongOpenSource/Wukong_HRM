package com.kakarote.hrm.constant.appraisal;

public enum StatusEnum {
    NORMAL(1, "正常"),
    ABNORMAL(0, "异常");
    private Integer value;
    private String name;

    StatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

}
