package com.kakarote.core.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.exception.NoLoginException;
import com.kakarote.core.redis.Redis;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangzhiwei
 * user注入切面
 */
@Aspect
@Component
@ConditionalOnClass(value = {ServletRequest.class, HandlerMapping.class})
public class ParamAspect implements Ordered {
    @Autowired
    private Redis redis;

    @Around("(execution(* com.kakarote.*.controller..*(..))||execution(* com.kakarote.*.*.controller..*(..))) && execution(@(org.springframework.web.bind.annotation.*Mapping) * *(..))  && !execution(@(com.kakarote.core.common.ParamAspect) * *(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        try {
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader(Const.DEFAULT_TOKEN_NAME);
                UserInfo info = new  UserInfo();
                if (StrUtil.isNotEmpty(token) && redis.exists(token+Const.TOKEN_CACHE_NAME)) {
                    info = redis.get(token);
                }
                if (ObjectUtil.isNull(info)) {
                    throw new NoLoginException();
                }
                UserUtil.setUser(info);
            }
            return point.proceed();
        } finally {
           UserUtil.removeUser();
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}