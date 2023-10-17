package com.kakarote.core.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 数据权限枚举
 *
 * @author zhangzhiwei
 */
public enum DataAuthEnum {
    /**
     * 数据权限 1、本人，2、本人及下属，3、本部门，4、本部门及下属部门，5、全部 0 自定义
     */
    MYSELF(1),
    MYSELF_AND_SUBORDINATE(2),
    THIS_DEPARTMENT(3),
    THIS_DEPARTMENT_AND_SUBORDINATE(4),
    ALL(5),
    CUSTOM(0);


    DataAuthEnum(Integer value) {
        this.value = value;
    }

    private final Integer value;

    public Integer getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DataAuthEnum parse(Integer value) {
        if (value == null) {
            return CUSTOM;
        }
        switch (value) {
            case 1:
                return MYSELF;
            case 2:
                return MYSELF_AND_SUBORDINATE;
            case 3:
                return THIS_DEPARTMENT;
            case 4:
                return THIS_DEPARTMENT_AND_SUBORDINATE;
            case 5:
                return ALL;
            case 0:
            default:
                return CUSTOM;
        }
    }

}
