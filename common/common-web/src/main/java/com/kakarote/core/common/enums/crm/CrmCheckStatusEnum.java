package com.kakarote.core.common.enums.crm;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description 审批 类型枚举
 * @Author UNIQUE
 * @Date 2022/11/14
 * @Param
 * @return
 **/

public enum CrmCheckStatusEnum {

    /**
     * 审批 类型枚举
     */
    CHECK_ING(0, "待审核"),
    CHECK_PASS(1, "通过"),
    CHECK_CAMAL(2, "拒绝"),
    CHECK_ON(3, "审核中"),
    CHECK_BACK(4, "已撤回"),
    CHECK_NO_PUSH(5, "未提交"),
    CHECK_VOID(8, "已作废"),
    CHECK_IGNORE(10, "正常");

    CrmCheckStatusEnum(Integer type, String remarks) {
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

    public static CrmCheckStatusEnum parse(Integer type) {
        for (CrmCheckStatusEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static CrmCheckStatusEnum parse(String name) {
        for (CrmCheckStatusEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return null;
    }

    public static List<Object> getEnumJsonList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmCheckStatusEnum crmEnum : values()) {
            enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
        }
        return enumList;
    }

    public static List<Object> getEnumJsonReceivablesList() {
        List<Object> enumList = new ArrayList<>();
        for (CrmCheckStatusEnum crmEnum : values()) {
            if (Arrays.asList(0, 1, 2, 3, 5, 10).contains(crmEnum.getType())) {
                enumList.add(new JSONObject().fluentPut("name", crmEnum.getRemarks()).fluentPut("value", crmEnum.getType()));
            }
        }
        return enumList;
    }
}
