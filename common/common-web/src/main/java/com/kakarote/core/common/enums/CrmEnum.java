package com.kakarote.core.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author zhangzhiwei
 * crm模块枚举
 */

public enum CrmEnum {

    /**
     * crm模块枚举
     */
    LEADS(1, "线索"),
    CUSTOMER(2, "客户"),
    CONTACTS(3, "联系人"),
    PRODUCT(4, "产品"),
    BUSINESS(5, "商机"),
    CONTRACT(6, "合同"),
    RECEIVABLES(7, "回款"),
    RECEIVABLES_PLAN(8, "回款计划"),
    CUSTOMER_POOL(9, "公海"),
    MARKETING(10, "市场活动"),
    RETURN_VISIT(17, "客户回访"),
    INVOICE(18, "发票"),
    ACTIVITY(19, "跟进记录"),
    LEADS_POOL(21, "线索池"),
    OUT_WORK_SIGN(22,"外勤签到"),
    NULL(0, "");

    CrmEnum(Integer type, String remarks) {
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

    public static CrmEnum parse(Integer type) {
        for (CrmEnum crmEnum : values()) {
            if (crmEnum.getType().equals(type)) {
                return crmEnum;
            }
        }
        return NULL;
    }

    public static CrmEnum parse(String name) {
        for (CrmEnum crmEnum : values()) {
            if (crmEnum.name().equals(name)) {
                return crmEnum;
            }
        }
        return NULL;
    }

    public String getIndex() {
        if (this == CrmEnum.LEADS) {
            return "crm_leads";
        }
        return "wukong_" + name().toLowerCase();
    }

    public String getTableName() {
        return name().toLowerCase();
    }

    /**
     * 获取主键ID
     *
     * @param camelCase 是否驼峰
     * @return primaryKey
     */
    public String getPrimaryKey(boolean camelCase) {
        String name;
        if (this == CrmEnum.RETURN_VISIT) {
            name = "visit";
        } else if (this == CrmEnum.ACTIVITY) {
            return "id";
        } else {
            name = name().toLowerCase();
        }
        if (camelCase) {
            return StrUtil.toCamelCase(name) + "Id";
        }
        return name + "_id";
    }

    public String getPrimaryKey() {
        return getPrimaryKey(true);
    }

    public String getLanguageKey() {
        return "{actionRecord." + StrUtil.toCamelCase(name().toLowerCase()) + "}";
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
