package com.kakarote.hrm.constant.appraisal;

/**
 * 考核计划状态枚举类
 */
public enum AppraisalStatusEnum {
    NO_START(2, "未开始"),
    RUNNING(3, "进行中"),
    PLACE_ON_FILE(4, "已归档"),
    TERMINATION(5, "终止");
    //状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档 5:考核终止
    private Integer value;
    private String name;

    AppraisalStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static AppraisalStatusEnum parse(int value) {
        for (AppraisalStatusEnum appraisalStatusEnum : AppraisalStatusEnum.values()) {
            if (appraisalStatusEnum.value == value) {
                return appraisalStatusEnum;
            }
        }
        return null;
    }


    public static String parseName(Integer type) {
        for (AppraisalStatusEnum value : AppraisalStatusEnum.values()) {
            if (value.value.equals(type)) {
                return value.name;
            }
        }
        return "";
    }
}
