package com.kakarote.hrm.constant.appraisal;

/**
 * 评分人类型枚举类
 */
public enum RaterTypeEnum {
    SUPERIOR(1, "上级"),
    DEPT_HEAD(2, "部门负责人"),
    APPOINTOR(3, "指定人"),
    SELF(4, "被考核人");
    //评分人类型：1上级2：部门负责人3：指定评分人4：被考核人
    private Integer value;
    private String name;

    RaterTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public static RaterTypeEnum parse(int value) {
        for (RaterTypeEnum raterTypeEnum : RaterTypeEnum.values()) {
            if (raterTypeEnum.value == value) {
                return raterTypeEnum;
            }
        }
        return null;
    }
}
