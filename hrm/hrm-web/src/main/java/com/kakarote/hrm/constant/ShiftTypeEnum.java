package com.kakarote.hrm.constant;

public enum ShiftTypeEnum {
    REST(0, "休息"),
    COMMON(1, "早晚打卡"),
    SUBSECTION(2, "分段打卡");

    ShiftTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }


    private int value;
    private String name;

    public static String parseName(int type) {
        for (ShiftTypeEnum value : ShiftTypeEnum.values()) {
            if (value.value == type) {
                return value.name;
            }
        }
        return "";
    }

    public static int valueOfType(String name) {
        for (ShiftTypeEnum value : ShiftTypeEnum.values()) {
            if (value.name.equals(name)) {
                return value.value;
            }
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
