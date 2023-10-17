package com.kakarote.hrm.constant;

import cn.hutool.core.util.StrUtil;
import com.kakarote.common.field.entity.FieldLabel;

import java.util.Objects;

/**
 * HRM模块枚举
 */
public enum HrmEnum implements FieldLabel {

    /**
     * 绩效考核
     */
    APPRAISAL_EMPLOYEE(5, "考核员工");
    /**
     * 类型
     */
    private final int type;

    /**
     * 名称
     */
    private final String remarks;

    HrmEnum(int type, String remarks) {
        this.type = type;
        this.remarks = remarks;
    }

    public static HrmEnum parse(Integer type) {
        for (HrmEnum hrmEnum : HrmEnum.values()) {
            if (Objects.equals(hrmEnum.getType(), type)) {
                return hrmEnum;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public String getIndex() {
        return "hrm_" + name().toLowerCase();
    }

    /**
     * 获取模块名称
     *
     * @return 模块名称
     */
    @Override
    public String getModelName() {
        return "hrm";
    }

    @Override
    public String getTableName() {
        return name().toLowerCase();
    }

    public String getTableId() {
        return StrUtil.toCamelCase(name().toLowerCase()) + "Id";
    }

    /**
     * 获取主键ID
     *
     * @param camelCase 是否驼峰
     * @return primaryKey
     */
    @Override
    public String getPrimaryKey(boolean camelCase) {
        String name = name().toLowerCase();
        if (camelCase) {
            return StrUtil.toCamelCase(name) + "Id";
        }
        return name + "_id";
    }

    @Override
    public String getPrimaryKey() {
        return getPrimaryKey(true);
    }
}
