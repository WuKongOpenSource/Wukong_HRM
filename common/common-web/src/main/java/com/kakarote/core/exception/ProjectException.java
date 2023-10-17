package com.kakarote.core.exception;

import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;

public class ProjectException extends RuntimeException implements ResultCode {

    private final String msg;
    private final int code;


    public ProjectException() {
        super("参数验证错误", null, false, false);
        this.code = SystemCodeEnum.SYSTEM_NO_VALID.getCode();
        this.msg = SystemCodeEnum.SYSTEM_NO_VALID.getMsg();
    }

    public ProjectException(int code, String msg) {
        super(code + ":" + msg, null, true, true);
        this.code = code;
        this.msg = msg;
    }

    public ProjectException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public ProjectException(ResultCode resultCode, Object... args) {
        this(resultCode.getCode(), String.format(resultCode.getMsg(), args));
    }

    public ProjectException(ResultCode resultCode, String str, Boolean flag) {
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
