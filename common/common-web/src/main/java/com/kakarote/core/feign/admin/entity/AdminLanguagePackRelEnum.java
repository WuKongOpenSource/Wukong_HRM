package com.kakarote.core.feign.admin.entity;


/**
 * 消息通知枚举类
 */
public enum AdminLanguagePackRelEnum {

    /**
     * 模块
     */
    FIELD(1,"业务自定义字段"),
    CODE_FIX(2,"代码固定字段"),
    SYS_FIX(3,"系统固定字段"),


    ;

    AdminLanguagePackRelEnum(Integer label, String desc){
        this.label=label;
        this.desc=desc;
    }
    //模块分类 CRM
    private final Integer  label;
    private final String desc;


    public String getDesc() {
        return desc;
    }

    public Integer getLabel() {
        return label;
    }

    public static AdminLanguagePackRelEnum getByLabel(Integer label) {
        for (AdminLanguagePackRelEnum Enum : AdminLanguagePackRelEnum.values()) {
            if (Enum.getLabel().equals(label)) {
                return Enum;
            }
        }
        return null;
    }
}
