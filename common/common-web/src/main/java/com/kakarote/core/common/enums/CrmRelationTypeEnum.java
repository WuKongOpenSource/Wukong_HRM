package com.kakarote.core.common.enums;

/**
 * @version 1.0
 * @program: wk_crm
 * @className OaExamineRelationTypeEnum
 * @description: oa crm 相互关系类型枚举 类型 2客户id 3联系人id 5商机id 6合同id 7回款id
 * @author: jiao sir
 * @create: 2021-11-19 15:57
 **/
public enum CrmRelationTypeEnum {
    MODULE(0),

    /**
     * 客户
     */
    CUSTOMER(2),

    /**
     * 联系人
     */
    CONTACTS(3),

    /**
     * 产品
     */
    PRODUCT(4),

    /**
     * 商机
     */
    BUSINESS(5),

    /**
     * 合同
     */
    CONTRACT(6),

    /**
     * 回款
     */
    RECEIVABLES(7),

    ;


    private int type;

    CrmRelationTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
