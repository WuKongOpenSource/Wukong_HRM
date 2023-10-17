package com.kakarote.hrm.constant;

public enum ClockStatusEnum {
    NORMAL(0, "正常"),
    LATE(1, "迟到"),
    EARLY(2, "早退"),
    OUT(3, "外勤"),
    OVERTIME(4, "加班");

    ClockStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }


    private int value;
    private String name;

    public static String parseName(int type) {
        for (ClockStatusEnum value : ClockStatusEnum.values()) {
            if (value.value == type) {
                return value.name;
            }
        }
        return "";
    }

    public static int valueOfType(String name) {
        for (ClockStatusEnum value : ClockStatusEnum.values()) {
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
