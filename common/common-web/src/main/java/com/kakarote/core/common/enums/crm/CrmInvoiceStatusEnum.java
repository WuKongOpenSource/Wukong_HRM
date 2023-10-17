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

public enum CrmInvoiceStatusEnum {

    /**
     * 审批 类型枚举
     */
    OFF(0, "未开票"),
    ON(1, "已开票"),
    ;

    CrmInvoiceStatusEnum(Integer type, String remarks) {
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

    public static CrmInvoiceStatusEnum parse(Integer type) {
        for (CrmInvoiceStatusEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmInvoiceStatusEnum parse(String name) {
        for (CrmInvoiceStatusEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmInvoiceStatusEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }
}
