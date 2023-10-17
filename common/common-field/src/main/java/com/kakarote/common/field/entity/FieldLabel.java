package com.kakarote.common.field.entity;

/**
 * 业务类型
 *
 * @author zhangzhiwei
 */
public interface FieldLabel {

    /**
     * 获取主键ID
     *
     * @param camelCase 是否驼峰
     * @return primaryKey
     */
    public String getPrimaryKey(boolean camelCase);

    /**
     * 获取驼峰的主键ID
     *
     * @return primaryKey
     */
    public String getPrimaryKey();

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName();

    /**
     * 业务表的类型int类型
     *
     * @return type
     */
    public Integer getType();

    /**
     * 业务表说明
     *
     * @return 业务表说明
     */
    public String getRemarks();


    /**
     * 获取索引名称
     *
     * @return 索引名称
     */
    public String getIndex();

    /**
     * 获取别名
     *
     * @return 别名
     */
    default String getAlias() {
        return null;
    }

    /**
     * 是否需要刷新索引
     *
     * @return 是否需要刷新索引
     */
    default boolean isRefreshIndex() {
        return true;
    }

    /**
     * 获取模块名称
     *
     * @return 模块名称
     */
    public String getModelName();
}
