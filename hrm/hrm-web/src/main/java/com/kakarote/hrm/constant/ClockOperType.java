package com.kakarote.hrm.constant;

public enum ClockOperType {

    APP(1, "APP打卡"),
    HR(2, "HR添加"),
    AUTO(3, "自动打卡");

    ClockOperType(int value, String name) {
        this.value = value;
        this.name = name;
    }


    private int value;
    private String name;

    public static String parseName(int type) {
        for (ClockOperType value : ClockOperType.values()) {
            if (value.value == type) {
                return value.name;
            }
        }
        return "";
    }

    public static int valueOfType(String name) {
        for (ClockOperType value : ClockOperType.values()) {
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
