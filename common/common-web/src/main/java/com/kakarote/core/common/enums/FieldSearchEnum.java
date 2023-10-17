package com.kakarote.core.common.enums;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;

import java.util.Arrays;


/**
 * 字段搜索枚举
 * @author zhangzhiwei
 */
@ApiModel(value = "字段搜索枚举")
public enum FieldSearchEnum {
    /**
     * 等于
     */
    IS(1),
    /**
     * 不等于
     */
    IS_NOT(2),
	/**
	 * 包含
	 */
	CONTAINS(3),
    /**
     * 不包含
     */
    NOT_CONTAINS(4),
	/**
	 * 为空
	 */
	IS_NULL(5),
	/**
	 * 不为空
	 */
	IS_NOT_NULL(6),
    /**
     * 大于
     */
    GT(7),
    /**
     * 大于等于
     */
    EGT(8),
    /**
     * 小于
     */
    LT(9),
    /**
     * 小于等于
     */
    ELT(10),
    /**
     * 通过id
     */
    ID(11),
    /**
     * 前缀匹配
     */
    PREFIX(12),
    /**
     * 后缀匹配
     */
    SUFFIX(13),
    /**
     * 数字区间
     */
    RANGE(14),

    /**
     * 不存在
     */
    NULL(0),


    SCRIPT(-1);

    FieldSearchEnum(Integer type) {
        this.type = type;
    }

    @JsonCreator
    public static FieldSearchEnum create(String type) {
        for (FieldSearchEnum searchEnum : values()) {
            if (searchEnum.getType().toString().equals(type)) {
                return searchEnum;
            }
        }
        return NULL;
    }

	public static FieldSearchEnum parse(Integer type) {
		return Arrays.stream(FieldSearchEnum.values())
				.filter(d -> ObjectUtil.equal(type, d.getType()))
				.findFirst().orElse(NULL);
	}

    private final Integer type;

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString() + ":" + name();
    }

    public String valueOf(Integer type) {
        return type.toString() + ":" + name();
    }
}
