package com.kakarote.core.exception;

import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;

public class JxcException extends RuntimeException implements ResultCode {

    private final String msg;
    private final int code;


    public JxcException() {
        super("参数验证错误", null, false, false);
        this.code = SystemCodeEnum.SYSTEM_NO_VALID.getCode();
        this.msg = SystemCodeEnum.SYSTEM_NO_VALID.getMsg();
    }

    public JxcException(int code, String msg) {
        super(code + ":" + msg, null, true, true);
        this.code = code;
        this.msg = msg;
    }

    public JxcException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public JxcException(ResultCode resultCode, Object... args) {
        this(resultCode.getCode(), String.format(resultCode.getMsg(), args));
    }

    public JxcException(ResultCode resultCode, String str, Boolean flag) {
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
