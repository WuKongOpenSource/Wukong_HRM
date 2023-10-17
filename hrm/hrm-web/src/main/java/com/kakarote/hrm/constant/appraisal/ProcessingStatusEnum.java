package com.kakarote.hrm.constant.appraisal;

//员工绩效模块处理状态枚举类
public enum ProcessingStatusEnum {
    //状态：0：未处理 1已处理 2待处理 3：驳回 4：重新处理
    NOT_PROCESSED(0, "未处理"),
    PROCESSED(1, "已处理"),
    PENDING(2, "待处理"),
    REJECT(3, "驳回"),
    IN_PROCESS(4, "处理中"),
    APPEALED(5, "已申诉");
    private Integer value;
    private String name;

    ProcessingStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }
}
