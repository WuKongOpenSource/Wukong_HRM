package com.kakarote.hrm.constant.appraisal;

/**
 * 可见内容
 */
public enum VisibleContentEnum {
    SELF(1, "仅自己的评分/评语"),
    ALL(2, "所有人的评分/评语");
    private Integer value;
    private String name;

    VisibleContentEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static VisibleContentEnum parse(int value) {
        for (VisibleContentEnum visibleContentEnum : VisibleContentEnum.values()) {
            if (visibleContentEnum.value == value) {
                return visibleContentEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }
}
