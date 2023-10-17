package com.kakarote.core.common.enums.crm;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description  审批 类型枚举
 * @Author UNIQUE
 * @Date 2022/11/14
 * @Param
 * @return
 **/

public enum CrmProductStatusEnum {

    /**
     * 审批 类型枚举
     */
    UP(1, "上架"),
    DOWN(0, "下架"),
    ;

    CrmProductStatusEnum(Integer type, String remarks) {
        this.type = type;
        this.remarks = remarks;
    }

    private final Integer type;
    private final String remarks;

    public String getRemarks() {
        return remarks;
    }

    public Integer getType() {
        return type;
    }

    public static CrmProductStatusEnum parse(Integer type) {
        for (CrmProductStatusEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmProductStatusEnum parse(String name) {
        for (CrmProductStatusEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmProductStatusEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }
}
