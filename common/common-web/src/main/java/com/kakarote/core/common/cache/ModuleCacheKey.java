package com.kakarote.core.common.cache;

/**
 * 无代码缓存 key
 *
 * @author : zjj
 * @since : 2022/9/2
 */
public interface ModuleCacheKey {

    /**
     * 模块版本缓存
     */
    String MODULE_VERSION_CACHE_NAME = "MODULE:VERSION:CACHE:";

    /**
     * 激活的模块缓存
     */
    String ACTIVE_MODULE_CACHE_NAME = "ACTIVE_MODULE:CACHE:";

    /**
     * 字段版本缓存
     */
    String FIELD_VERSION_CACHE_NAME = "FIELD:VERSION:CACHE:";

    /**
     * 模块字段缓存
     */
    String MODULE_FIELD_CACHE_NAME = "MODULE:FIELD:CACHE:";

    /**
     * 节点版本缓存
     */
    String FLOW_VERSION_CACHE_NAME = "FLOW:VERSION:CACHE:";
}
