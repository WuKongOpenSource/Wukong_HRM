package com.kakarote.hrm.constant.appraisal;

public enum AppraisalQuotaType {
    ACHIEVEMENT_QUOTA(1, "业绩指标"),
    BEHAVIOR_ATTITUDE(2, "行为态度指标");

    private Integer value;
    private String name;

    AppraisalQuotaType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
