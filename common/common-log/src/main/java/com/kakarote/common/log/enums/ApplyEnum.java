package com.kakarote.common.log.enums;

import lombok.Getter;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/14
 */
@Getter
public enum ApplyEnum {
    CRM(1, "CRM", false),
    ADMIN(2, "系统管理", false),
    OA(3, "OA", false),
    JXC(4, "进销存", false),
    HRM(5, "HRM", false),
    PM(6, "项目管理", false),
    FS(12, "财务管理", false),

    SYSTEM_SETTING(7, "系统设置", true),
    ROLE_PERMISSION_MANAGEMENT(8, "角色权限管理", true),
    PROJECT_MANAGEMENT(9, "项目管理", true),
    OFFICE_MANAGEMENT(10, "办公管理", true),
    CUSTOMER_MANAGEMENT(11, "客户管理", true),
    HUMAN_RESOURCE_MANAGEMENT(13, "人力资源管理", true),
    JXC_MANAGEMENT(14, "进销存管理", true),
    FS_MANAGEMENT(15, "财务管理", true),

    NULL(0, "", false);;

    private final Integer type;
    private final String remarks;
    private final Boolean module;


    ApplyEnum(Integer type, String remarks, boolean module) {
        this.type = type;
        this.remarks = remarks;
        this.module = module;
    }

    public static ApplyEnum parse(Integer type) {
        for (ApplyEnum value : ApplyEnum.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return NULL;
    }

}
