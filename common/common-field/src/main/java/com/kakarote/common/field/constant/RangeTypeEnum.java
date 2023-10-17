package com.kakarote.common.field.constant;

import lombok.Getter;

/**
 * 区间查询所使用的枚举
 *
 * @author admin
 */
@Getter
public enum RangeTypeEnum {

    /**
     * 区间查询类型
     */
    GT("大于"),

    GTE("大于等于"),

    LT("小于"),

    LTE("小于等于");

    private final String name;

    RangeTypeEnum(String name) {
        this.name = name;
    }
}
