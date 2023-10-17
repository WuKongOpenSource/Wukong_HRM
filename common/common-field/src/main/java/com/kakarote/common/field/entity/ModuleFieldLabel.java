package com.kakarote.common.field.entity;

/**
 * 无代码业务类型实现
 *
 * @author zhangzhiwei
 */
public class ModuleFieldLabel implements FieldLabel {


    /**
     * 是否刷新索引
     */
    private Boolean isRefreshIndex;

    /**
     * 无代码业务实现
     *
     * @param moduleId 模块ID
     */
    public ModuleFieldLabel(Long moduleId) {
    }

    /**
     * 获取主键ID
     *
     * @param camelCase 是否驼峰
     * @return primaryKey
     */
    @Override
    public String getPrimaryKey(boolean camelCase) {
        if (camelCase) {
            return "dataId";
        } else {
            return "data_id";
        }
    }

    /**
     * 获取驼峰的主键ID
     *
     * @return primaryKey
     */
    @Override
    public String getPrimaryKey() {
        return getPrimaryKey(true);
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    @Override
    public String getTableName() {
        return "wk_module_field_data";
    }

    /**
     * 业务表的类型int类型
     *
     * @return type
     */
    @Override
    public Integer getType() {
        return 0;
    }

    /**
     * 业务表说明
     *
     * @return 业务表说明
     */
    @Override
    public String getRemarks() {
        return "无代码业务实现";
    }

    /**
     * 获取索引名称
     *
     * @return 索引名称
     */
    @Override
    public String getIndex() {
        return "module_2021";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModuleFieldLabel{");
        sb.append("indexName:").append(getIndex());
        sb.append("remarks:").append(getRemarks());
        sb.append('}');
        return sb.toString();
    }

    /**
     * 获取索引名称
     *
     * @param moduleId 模块ID
     * @return 索引名称
     */
    public static String getIndexName(Long moduleId) {
        return "module_2021";
    }

    @Override
    public boolean isRefreshIndex() {
        if (isRefreshIndex == null) {
            return true;
        }
        return isRefreshIndex;
    }

    /**
     * 获取模块名称
     *
     * @return 模块名称
     */
    @Override
    public String getModelName() {
        return "module";
    }

    public ModuleFieldLabel setRefreshIndex(Boolean refreshIndex) {
        isRefreshIndex = refreshIndex;
        return this;
    }
}
