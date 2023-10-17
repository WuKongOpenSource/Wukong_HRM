package com.kakarote.hrm.constant.appraisal;

public enum AppraisalOperationStatusEnum {
    OPEN_SCORING(1, "开启评分"),
    OPEN_INTERVIEW(2, "开启绩效面谈"),
    OPEN_FILE(3, "归档");

    private Integer value;
    private String name;

    AppraisalOperationStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
