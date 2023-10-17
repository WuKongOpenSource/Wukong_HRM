package com.kakarote.core.common.enums;

import lombok.Getter;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/7/20
 */
@Getter
public enum AccessTypeEnum {

    MOBILE("mobile", "手机访问", 1),

    WEB("web", "浏览器访问", 2);

    private final String remarks;

    private final String sign;

    private final Integer type;

    AccessTypeEnum(String sign, String remarks, Integer type) {
        this.sign = sign;
        this.remarks = remarks;
        this.type = type;
    }

    public static AccessTypeEnum parse(String sign) {
        for (AccessTypeEnum typeEnum : values()) {
            if (typeEnum.getSign().equals(sign)) {
                return typeEnum;
            }
        }
        return WEB;
    }

    public static AccessTypeEnum parse(Integer type) {
        for (AccessTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return WEB;
    }


}
