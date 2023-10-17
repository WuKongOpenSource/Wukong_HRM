package com.kakarote.hrm.constant.appraisal;

/**
 * 超期未处理类型
 */
public enum OverdueTypeEnum {
    REFUSE(1, "未审批拒绝"),
    PASS(2, "未审批通过");
    private Integer value;
    private String name;

    OverdueTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public static OverdueTypeEnum parse(int value) {
        for (OverdueTypeEnum overdueTypeEnum : OverdueTypeEnum.values()) {
            if (overdueTypeEnum.value == value) {
                return overdueTypeEnum;
            }
        }
        return null;
    }
}
