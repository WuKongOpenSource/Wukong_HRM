package com.kakarote.hrm.common.admin;

/**
 * 角色类型枚举
 *
 * @author zhangzhiwei
 */
public enum AdminRoleTypeEnum {
    /**
     * 自定义
     */
    CUSTOM(0, "custom", "自定义角色"),
    MANAGER(1, "manage", "系统管理角色"),
    CUSTOMER_MANAGER(2, "crm", "客户管理角色"),
    FINANCE(4, "finance", "财务管理角色"),
    WORK(5, "work", "项目管理角色"),
    OA(7, "oa", "办公管理角色"),
    PROJECT(8, "project", "项目管理角色"),
    HRM(9, "hrm", "人力资源管理角色"),
    JXC(10, "jxc", "进销存管理角色"),
    SCRM(12, "scrm", "scrm管理角色"),
    CS(13, "cs", "营销管理角色"),
    WP(14,"wp","WordPress管理角色");

    AdminRoleTypeEnum(Integer type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
    }


    /**
     * 类型
     */
    private final Integer type;

    /**
     * 名称
     */
    private final String name;

    /**
     * 备注
     */
    private final String desc;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }


    public static AdminRoleTypeEnum parse(Integer type) {
        for (AdminRoleTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

}
