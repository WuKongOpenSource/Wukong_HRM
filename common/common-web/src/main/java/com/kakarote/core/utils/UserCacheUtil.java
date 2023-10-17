package com.kakarote.core.utils;

import com.alicp.jetcache.CacheManager;
import com.kakarote.core.redis.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zhangzhiwei
 * 用户缓存相关方法
 */
@Component
@ConditionalOnClass(CacheManager.class)
public class UserCacheUtil {

    static UserCacheUtil ME;
    @PostConstruct
    public void init() {
        ME = this;
    }
    @Autowired
    Redis redis;
}
