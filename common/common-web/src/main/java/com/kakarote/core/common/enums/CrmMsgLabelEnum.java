package com.kakarote.core.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * crm消息标签
 * @author zjj
 * @ClassName CrmMsgLabelEnum.java
 * @Description crm消息标签
 * @createTime 2021-09-17
 */
public enum CrmMsgLabelEnum {
    leads("线索"),
    customer("客户"),
    contacts("联系人"),
    business("商机"),
    contract("合同"),
    receivables("回款"),
    receivables_plan("回款计划"),
    invoice("发票"),
    product("产品"),
    return_visit("回访"),
    marketing("市场活动"),
    NULL("")
    ;

    @Getter
    private String desc;

    CrmMsgLabelEnum(String desc) {
        this.desc = desc;
    }

    /**
     * 消息标签
     *
     * @return
     */
    public static List<String> labels(){
        List<String> labels = new ArrayList<>();
        for (CrmMsgLabelEnum labelEnum : CrmMsgLabelEnum.values()) {
            labels.add(labelEnum.name());
        }
        return labels;
    }

    public static CrmMsgLabelEnum parse(String name) {
        return Arrays.stream(CrmMsgLabelEnum.values()).filter(e -> StrUtil.equals(name, e.name())).findFirst().orElse(null);
    }
}
