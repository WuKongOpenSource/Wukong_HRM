package com.kakarote.core.exception;

import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;

/**
 * crm默认的异常处理
 *
 * @author Administrator
 */
public class ScrmException extends RuntimeException implements ResultCode {

    private final String msg;
    private final int code;


    public ScrmException() {
        super("参数验证错误", null, false, false);
        this.code = SystemCodeEnum.SYSTEM_NO_VALID.getCode();
        this.msg = SystemCodeEnum.SYSTEM_NO_VALID.getMsg();
    }

    public ScrmException(int code, String msg) {
        super(code + ":" + msg, null, true, true);
        this.code = code;
        this.msg = msg;
    }

    public ScrmException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public ScrmException(ResultCode resultCode, Object... args) {
        this(resultCode.getCode(), String.format(resultCode.getMsg(), args));
    }

    public ScrmException(ResultCode resultCode, String str, Boolean flag) {
        this(resultCode.getCode(), resultCode.getMsg() + ":" + str);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
