package com.kakarote.core.common.enums;

/**
 * @author zjj
 * @ClassName CrmMsgActionEnum.java
 * @Description 消息动作
 * @createTime 2021-09-17
 */
public enum CrmMsgActionEnum {
    // 新建
    save,
    // 转移
    transfer,
    // 转化为客户
    transform,
    // 添加团队成员
    addTermMember,
    // 移除团队成员
    removeTermMember,
    // 标记、重置开票
    updateInvoiceStatus,
    // 导入
    excelImport,
    // 导出
    excelExport,
    ;
}
