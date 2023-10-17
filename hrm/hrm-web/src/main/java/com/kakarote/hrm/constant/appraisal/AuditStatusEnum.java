package com.kakarote.hrm.constant.appraisal;

/**
 * 审核状态枚举类
 */
public enum AuditStatusEnum {
    //0未审核 1已审核 2:驳回 3：待审核
    UN_AUDITED(0, "未审核"),
    AUDITED(1, "已审核"),
    REJECT(2, "驳回"),
    PENDING(3, "待审核"),
    UNDER_REVIEW(4, "审核中");
    private Integer value;
    private String name;

    AuditStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
