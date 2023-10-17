package com.kakarote.hrm.constant;

/**
 * 考勤类型
 */
public enum AttendanceResultEnum {
    ALL(-1, ""),
    NORMAL(0, "正常"),
    LATE(1, "迟到"),
    EARLY(2, "早退"),
    MISSCARD(3, "缺卡"),
    ABSENTEEISM(4, "旷工"),
    OUT(5, "外勤"),
    LEAVE(6, "请假"),
    OVERTIME(7, "加班"),
    TRAVEL(8, "出差"),
    REST(9, "休息");

    AttendanceResultEnum(int value, String name) {
        this.name = name;
        this.value = value;
    }

    public static String parseName(int type) {
        for (AttendanceResultEnum value : AttendanceResultEnum.values()) {
            if (value.value == type) {
                return value.name;
            }
        }
        return "";
    }

    public static AttendanceResultEnum parse(int type) {
        for (AttendanceResultEnum value : AttendanceResultEnum.values()) {
            if (value.value == type) {
                return value;
            }
        }
        return ALL;
    }

    private int value;
    private String name;


    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }


}
