package com.kakarote.core.feign.admin.entity;


/**
 * 消息通知枚举类
 */
public enum ModuleTypeEnum {

    /**
     * 模块
     */
    ADMIN(1,"admin"),
    CRM(2,"crm"),
    HRM(3,"hrm"),
    JXC(4,"jxc"),


    ;

    ModuleTypeEnum(Integer label, String desc){
        this.label=label;
        this.desc=desc;
    }
    //模块分类 CRM
    private final Integer  label;
    private final String desc;


    public String getDesc() {
        return desc;
    }

    public int getLabel() {
        return label;
    }

    public static ModuleTypeEnum getByLabel(Integer label) {
        for (ModuleTypeEnum Enum : ModuleTypeEnum.values()) {
            if (Enum.getLabel() ==label) {
                return Enum;
            }
        }
        return null;
    }
}
