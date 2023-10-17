package com.kakarote.core.utils;

import com.kakarote.core.entity.MsgBodyBO;

/**
 * @author 10323
 * @description: 消息通知
 * @date 2021/9/2510:52
 */
public class AdminMessageUtil {
    private static ThreadLocal<MsgBodyBO> threadLocal = new ThreadLocal<>();

    public static MsgBodyBO getMsgBody() {
        return threadLocal.get();
    }

    public static void setMsgBody(MsgBodyBO msgBody) {
        threadLocal.set(msgBody);
    }

    public static void remove(){
        threadLocal.remove();
    }
}
