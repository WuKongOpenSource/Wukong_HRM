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

public enum CrmReceivablesPlanStatusEnum {

    /**
     * 审批 类型枚举
     */
    RECEIVABLE_ING(0, "待回款"),
    RECEIVABLE_PASS(1, "回款完成"),
    RECEIVABLE_ONES(2, "部分回款"),
    RECEIVABLE_VOID(3, "作废"),
    RECEIVABLE_OVER(4, "逾期"),
    RECEIVABLE_NO_EFFECTIVE(5, "待生效"),
    ;

    CrmReceivablesPlanStatusEnum(Integer type, String remarks) {
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

    public static CrmReceivablesPlanStatusEnum parse(Integer type) {
        for (CrmReceivablesPlanStatusEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmReceivablesPlanStatusEnum parse(String name) {
        for (CrmReceivablesPlanStatusEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmReceivablesPlanStatusEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }
}
