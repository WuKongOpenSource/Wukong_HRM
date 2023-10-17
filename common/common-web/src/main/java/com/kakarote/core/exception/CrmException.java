package com.kakarote.core.exception;

import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;

/**
 * crm默认的异常处理
 *
 * @author Administrator
 */
public class CrmException extends RuntimeException implements ResultCode {

    private final String msg;
    private final int code;


    public CrmException() {
        super("参数验证错误", null, false, false);
        this.code = SystemCodeEnum.SYSTEM_NO_VALID.getCode();
        this.msg = SystemCodeEnum.SYSTEM_NO_VALID.getMsg();
    }

    public CrmException(int code, String msg) {
        super(code + ":" + msg, null, true, true);
        this.code = code;
        this.msg = msg;
    }

    public CrmException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public CrmException(ResultCode resultCode, Object... args) {
        this(resultCode.getCode(), String.format(resultCode.getMsg(), args));
    }

    public CrmException(ResultCode resultCode, String str, Boolean flag) {
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
