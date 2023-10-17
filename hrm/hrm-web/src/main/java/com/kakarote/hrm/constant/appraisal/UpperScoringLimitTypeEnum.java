package com.kakarote.hrm.constant.appraisal;

/**
 * 评分上限类型枚举类
 */
public enum UpperScoringLimitTypeEnum {
    UNIFIED(1, "统一上限");
    private Integer value;
    private String name;

    UpperScoringLimitTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
