package com.kakarote.core.common.annotation;

import com.kakarote.core.common.enums.CrmMsgActionEnum;
import com.kakarote.core.common.enums.CrmMsgLabelEnum;

import java.lang.annotation.*;

/**
 * @author 10323
 * @description: 消息注解
 * @date 2021/9/2510:04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Message {

    CrmMsgLabelEnum label();

    CrmMsgActionEnum action();
}
