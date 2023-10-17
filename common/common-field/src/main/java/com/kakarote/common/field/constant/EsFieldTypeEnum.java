package com.kakarote.common.field.constant;

import lombok.Getter;

/**
 * 目前支持的es字段类型
 *
 * @author zhangzhiwei
 */
@Getter
public enum EsFieldTypeEnum {
    /**
     * 普通单行文本
     */
    KEYWORD(1),
    /**
     * 日期
     */
    DATE(2),
    /**
     * 数字
     */
    NUMBER(3),
    /**
     * 嵌套类型
     */
    NESTED(4),
    /**
     * 日期时间
     */
    DATETIME(5),
    /**
     * 多行文本
     */
    TEXT(6),
    /**
     * 关联类型
     */
    FLATTENED(7);

    /**
     * 下标
     */
    private final int index;

    EsFieldTypeEnum(int index) {
        this.index = index;
    }

    /**
     * 根据下标获取枚举
     *
     * @param index 下标
     * @return 枚举
     */
    public static EsFieldTypeEnum parse(int index) {
        for (EsFieldTypeEnum value : EsFieldTypeEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return EsFieldTypeEnum.KEYWORD;
    }
}
