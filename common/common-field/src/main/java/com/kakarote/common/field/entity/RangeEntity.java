package com.kakarote.common.field.entity;

import com.kakarote.common.field.constant.RangeTypeEnum;
import lombok.Getter;

/**
 * 区间查询所使用的实体类
 *
 * @author admin
 */

@Getter
public class RangeEntity {
    /**
     * 值
     */
    private final Object value;

    /**
     * 类型
     */
    private final RangeTypeEnum type;

    public RangeEntity(Object value, RangeTypeEnum type) {
        this.value = value;
        this.type = type;
    }
}
