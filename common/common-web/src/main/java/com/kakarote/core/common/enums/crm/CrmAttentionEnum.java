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

public enum CrmAttentionEnum {

    /**
     * 审批 类型枚举
     */
    ONE(1, "一星"),
    TWO(2, "二星"),
    THREE(3, "三星"),
    FOUR(4, "四星"),
    FIVE(5, "五星"),
    ;

    CrmAttentionEnum(Integer type, String remarks) {
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

    public static CrmAttentionEnum parse(Integer type) {
        for (CrmAttentionEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmAttentionEnum parse(String name) {
        for (CrmAttentionEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmAttentionEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }
}
