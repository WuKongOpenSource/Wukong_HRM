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

public enum CrmInvoiceTypeEnum {

    /**
     * 审批 类型枚举
     */
    SPECIAL(1, "增值税专用发票"),
    ORDINARY (2, "增值税普通发票"),
    GENERAL (3, "国税通用机打发票"),
    LOCAL(4, "地税通用机打发票"),
    RECEIPT(5, "收据"),
    ;

    CrmInvoiceTypeEnum(Integer type, String remarks) {
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

    public static CrmInvoiceTypeEnum parse(Integer type) {
        for (CrmInvoiceTypeEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmInvoiceTypeEnum parse(String name) {
        for (CrmInvoiceTypeEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }
    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmInvoiceTypeEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }
}
