package com.kakarote.hrm.constant.appraisal;

//员工绩效阶段处理状态
public enum ActivateStatusEnum {
    RUNNING(1, "进行中"),
    COMPLETE(2, "已完成");

    private Integer value;
    private String name;

    ActivateStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
