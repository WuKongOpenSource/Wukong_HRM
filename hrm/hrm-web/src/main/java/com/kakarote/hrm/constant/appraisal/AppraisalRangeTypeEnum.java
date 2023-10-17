package com.kakarote.hrm.constant.appraisal;

/**
 * 考核范围类型枚举
 */
public enum AppraisalRangeTypeEnum {//新建考核计划-考核范围类型1:员工与部门 2：聘用形式
    EMPLOYEE_DEPT(1, "员工部门"),
    EMPLOYEE_TYPE(2, "聘用形式");
    private Integer value;
    private String name;

    AppraisalRangeTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public static AppraisalRangeTypeEnum parse(int value) {
        for (AppraisalRangeTypeEnum employeeTypeEnum : AppraisalRangeTypeEnum.values()) {
            if (employeeTypeEnum.value == value) {
                return employeeTypeEnum;
            }
        }
        return null;
    }
}
