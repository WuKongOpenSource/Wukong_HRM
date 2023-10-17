package com.kakarote.common.log.enums;

import lombok.Getter;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/15
 */
@Getter
public enum OperateTypeEnum {

    GENERAL(0, "普通操作"),

    SETTING(1, "设置"),

    IMPORT(2, "导入"),

    EXPORT(3, "导出"),

    LOGIN(4, "登录"),
    ;
    private final Integer type;
    private final String remarks;

    OperateTypeEnum(Integer type, String remarks) {
        this.type = type;
        this.remarks = remarks;
    }
}
