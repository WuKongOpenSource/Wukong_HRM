package com.kakarote.core.exception;

/**
 * 用户尚未登陆
 */
public class NoLoginException extends RuntimeException {
    public NoLoginException() {
        super("请先登录！", null, false, false);
    }
}
