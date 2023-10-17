package com.kakarote.common.field.constant;

/**
 * 自定义字段筛选的一些常量配置
 *
 * @author zhangzhiwei
 */
public class FieldConst {

    /**
     * bulk接口最大重试次数
     */
    public static final int BULK_RETRY_NUM = 3;

    /**
     * 超过多少页之后不允许跳页
     */
    public static final int MAX_ALLOW_SKIP_NUMBER = 100;

    /**
     * 脚本搜索key
     */
    public static final String SCRIPT_KEY = "scriptSearch";

    /**
     * searchAfterKey
     */
    public static final String SEARCH_AFTER_KEY = "searchAfter";
}
