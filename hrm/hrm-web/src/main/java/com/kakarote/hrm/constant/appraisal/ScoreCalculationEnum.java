package com.kakarote.hrm.constant.appraisal;

/**
 * 总分计算方式
 */
public enum ScoreCalculationEnum {
    WEIGHTING(1, "加权计算");
    private Integer value;
    private String name;

    ScoreCalculationEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
